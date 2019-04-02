package edu.duke.rs.baseProject.user;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import edu.duke.rs.baseProject.model.BaseEntity;
import edu.duke.rs.baseProject.role.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@NamedEntityGraphs({
	@NamedEntityGraph(name = "user.userAndRoles", attributeNodes = {
			@NamedAttributeNode("roles")
	})
})
public class User extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "user_id")
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQ", allocationSize = 100)
	private Long id;
	
	@Column(name = "user_name", length = 30, nullable = false)
	@NonNull
	@NotBlank
	@Size(min = 8, max = 30)
	private String userName;
	
	@Column(name = "password", length = 200, nullable = false)
	@NonNull
	@NotBlank
	@Size(min = 8, max = 200)
	private String password;
	
	@Column(name = "last_logged_in")
	private LocalDateTime lastLoggedIn;
	
	@ManyToMany
	@JoinTable(name = "users_to_roles",
		joinColumns = @JoinColumn(name = "user_fk", referencedColumnName = "user_id"),
		inverseJoinColumns = @JoinColumn(name = "role_fk", referencedColumnName = "role_id")
	)
	@NonNull
	@NotEmpty
	private Set<Role> roles;
}
