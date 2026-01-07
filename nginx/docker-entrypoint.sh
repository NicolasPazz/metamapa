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
    
    # Actualizar dominio en la configuración
    sed -i "s|metamapa-dds.ddns.net|${DOMAIN}|g" /etc/nginx/nginx.conf
    
    if [ -f "$CERT_PATH" ]; then
        # Actualizar rutas de certificados SSL
        sed -i "s|/etc/letsencrypt/live/metamapa-dds.ddns.net|/etc/letsencrypt/live/${DOMAIN}|g" /etc/nginx/nginx.conf
        
        # Agregar redirección HTTP->HTTPS si no existe (después de server_name en bloque HTTP)
        if ! grep -q "return 301 https" /etc/nginx/nginx.conf; then
            sed -i "/server_name.*${DOMAIN}/a\        return 301 https://\$server_name\$request_uri;" /etc/nginx/nginx.conf
        fi
        
        echo "✓ Certificados SSL detectados para ${DOMAIN}: HTTPS habilitado"
        return 0
    else
        # Si no hay certificados, comentar el bloque HTTPS completo
        # Método simple: comentar todas las líneas desde "server {" que contiene "listen 443" hasta su cierre
        # Usar un enfoque más simple: comentar líneas relacionadas con HTTPS
        sed -i '/^[[:space:]]*server[[:space:]]*{/,/^[[:space:]]*}[[:space:]]*$/{
            /listen[[:space:]]*443/{
                :loop
                s/^\([[:space:]]*\)\([^#].*\)$/\1# \2/
                n
                /^[[:space:]]*}[[:space:]]*$/!b loop
            }
        }' /etc/nginx/nginx.conf 2>/dev/null || {
            # Si el método anterior falla, usar un enfoque más simple
            # Comentar líneas que contengan palabras clave de HTTPS
            sed -i '/listen.*443/s/^\([[:space:]]*\)\([^#].*\)$/\1# \2/' /etc/nginx/nginx.conf
            sed -i '/ssl_certificate/s/^\([[:space:]]*\)\([^#].*\)$/\1# \2/' /etc/nginx/nginx.conf
            sed -i '/ssl_protocols/s/^\([[:space:]]*\)\([^#].*\)$/\1# \2/' /etc/nginx/nginx.conf
        }
        
        echo "⚠ Certificados SSL no detectados para ${DOMAIN}: sirviendo solo HTTP"
        return 1
    fi
}

# Configurar nginx inicialmente
configure_nginx
HAD_CERTS=$?

# Validar configuración antes de iniciar
if ! nginx -t; then
    echo "❌ Error: La configuración de nginx es inválida"
    exit 1
fi

# Iniciar nginx en background para poder recargarlo
nginx -g 'daemon off;' &
NGINX_PID=$!

# Esperar un momento para verificar que nginx inició correctamente
sleep 2
if ! kill -0 $NGINX_PID 2>/dev/null; then
    echo "❌ Error: Nginx no pudo iniciar"
    exit 1
fi

echo "✓ Nginx iniciado correctamente (PID: $NGINX_PID)"

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

