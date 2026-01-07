package ar.utn.ba.ddsi.users.domain;

import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = true)
    private String apellido;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @ElementCollection
    @CollectionTable(name = "usuario_rol", 
        joinColumns = @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    )
    @Column(name = "rol")
    private List<Rol> roles;
}
