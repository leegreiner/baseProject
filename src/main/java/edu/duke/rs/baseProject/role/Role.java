package edu.duke.rs.baseProject.role;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;

import edu.duke.rs.baseProject.model.BaseEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@Entity
@Table(name = "role",
  indexes = {
      @Index(name = "UIX_ROLE_ALT_ID", unique = true, columnList="alternate_id")
  }
)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@Immutable
@Audited
public class Role extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "role_id")
	@Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
  @SequenceGenerator(name="role_seq", sequenceName = "ROLE_SEQ", allocationSize = 1)
	private Long id;
	
	@Column(length = 30, updatable = false, unique = true, nullable = false)
	@NonNull
	private RoleName name;
}
