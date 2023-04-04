package edu.duke.rs.baseProject.role;

import java.io.Serializable;
import java.util.Set;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;

import edu.duke.rs.baseProject.model.BaseEntity;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "role",
  indexes = {
      @Index(name = "UIX_ROLE_ALT_ID", unique = true, columnList="alternate_id")
  }
)
@NamedEntityGraphs({
  @NamedEntityGraph(
      name = RoleConstants.ROLE_AND_PRIVILEGES_ENTITY_GRAPH, 
      attributeNodes = @NamedAttributeNode(value = "privileges"))
})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Immutable
@Audited
public class Role extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "role_id")
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 30)
	@NonNull
	@Convert(converter = RoleName.Converter.class)
	private RoleName name;
	
	@ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
    name = "roles_to_privileges", 
    joinColumns = @JoinColumn(name = "role_fk", referencedColumnName = "role_id"), 
    inverseJoinColumns = @JoinColumn(name = "privilege_fk", referencedColumnName = "privilege_id"))
	@ToString.Exclude
	private Set<Privilege> privileges;
}
