#!/bin/sh
# Entrypoint idempotente para nginx
# Funciona con o sin certificados SSL
# Detecta automáticamente cuando certbot obtiene certificados

# Variable de dominio (por defecto del nginx.conf, pero puede ser sobrescrita)
DOMAIN=${CERTBOT_DOMAIN:-metamapa-dds.ddns.net}
CERT_PATH="/etc/letsencrypt/live/${DOMAIN}/fullchain.pem"

# Función para configurar nginx
configure_nginx() {
    cp /etc/nginx/nginx.conf.template /etc/nginx/nginx.conf
    
    if [ -f "$CERT_PATH" ]; then
        sed -i "s|# REDIRECT_TO_HTTPS|return 301 https://\$server_name\$request_uri;|" /etc/nginx/nginx.conf
        sed -i "s|metamapa-dds.ddns.net|${DOMAIN}|g" /etc/nginx/nginx.conf
        echo "✓ Certificados SSL detectados para ${DOMAIN}: redirección HTTP->HTTPS habilitada"
        return 0
    else
        echo "⚠ Certificados SSL no detectados para ${DOMAIN}: sirviendo HTTP (certbot los obtendrá automáticamente)"
        return 1
    fi
}

# Configurar nginx inicialmente
configure_nginx
HAD_CERTS=$?

# Iniciar nginx en background para poder recargarlo
nginx -g 'daemon off;' &
NGINX_PID=$!

# Función para recargar nginx
reload_nginx() {
    if kill -0 $NGINX_PID 2>/dev/null; then
        nginx -s reload
        echo "✓ Nginx recargado con nueva configuración SSL"
    fi
}

# Monitorear certificados (idempotente: solo recarga si cambian)
while kill -0 $NGINX_PID 2>/dev/null; do
    sleep 10
    
    if [ -f "$CERT_PATH" ]; then
        if [ $HAD_CERTS -ne 0 ]; then
            echo "✓ Certificados SSL detectados por primera vez, recargando nginx..."
            configure_nginx
            reload_nginx
            HAD_CERTS=0
        fi
    else
        if [ $HAD_CERTS -eq 0 ]; then
            echo "⚠ Certificados SSL removidos, recargando nginx..."
            configure_nginx
            reload_nginx
            HAD_CERTS=1
        fi
    fi
done

wait $NGINX_PID

