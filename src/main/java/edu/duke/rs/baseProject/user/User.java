package edu.duke.rs.baseProject.user;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

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
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import edu.duke.rs.baseProject.model.BaseEntity;
import edu.duke.rs.baseProject.role.Role;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Entity
@Audited
@Table(name = "users")
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
  @SequenceGenerator(name = "user_seq", sequenceName = "USER_SEQ", allocationSize = 1)
	private Long id;
	
	@Column(name = "user_name", length = 30, nullable = false)
	@NonNull
	@NotBlank
	@Size(min = 4, max = 30)
	@Include
	private String userName;
	
	@Column(name = "password", length = 200, nullable = false)
	@NonNull
	@NotBlank
	@Size(min = 8, max = 200)
	private String password;
	
	@Column(name = "first_name", length = 30, nullable = false)
	@NonNull
	@NotBlank
	@Size(max = 30)
	private String firstName;
	
	@Column(name = "middle_initial", length = 1)
  @Size(max = 1)
	private String middleInitial;
	
	@Column(name = "last_name", length = 30, nullable = false)
  @NonNull
  @NotBlank
  @Size(max = 30)
	private String lastName;
	
	@Column(name = "email", length = 320, nullable = false)
  @NonNull
  @NotBlank
  @Size(max = 320)
  private String email;
	
	@Column(name = "account_enabled", nullable = false)
	@Type(type = "yes_no")
	private boolean accountEnabled = true;
	
	@Column(name = "time_zone", length = 100, nullable = false)
	@NotNull
	private TimeZone timeZone = TimeZone.getTimeZone("UTC");
	
	@Column(name = "last_logged_in")
	private LocalDateTime lastLoggedIn;
	
	@Column(name = "last_password_change")
  private LocalDateTime lastPasswordChange;
	
	@Column(name = "password_change_id", length = 36, nullable = true)
	@Type(type = "uuid-char")
	private UUID passwordChangeId;
	
	@Column(name = "password_chg_id_create_time", nullable = true)
	private LocalDateTime passwordChangeIdCreationTime;
	
	@ManyToMany
	@JoinTable(name = "users_to_roles",
		joinColumns = @JoinColumn(name = "user_fk", referencedColumnName = "user_id"),
		inverseJoinColumns = @JoinColumn(name = "role_fk", referencedColumnName = "role_id")
	)
	@NonNull
	@NotEmpty
	@ToString.Exclude
	private Set<Role> roles;
	
	public String getDisplayName() {
	  final StringBuffer buf = new StringBuffer();
	  
	  if (StringUtils.isNotBlank(firstName)) {
	    buf.append(firstName + " ");
	  }
	  
	  if (StringUtils.isNotBlank(middleInitial)) {
      buf.append(middleInitial + " ");
    }
	  
	  if (StringUtils.isNotBlank(lastName)) {
      buf.append(lastName);
    }
	  
	  return buf.toString();
	}
}
