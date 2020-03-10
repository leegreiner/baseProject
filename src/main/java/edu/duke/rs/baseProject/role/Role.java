package edu.duke.rs.baseProject.role;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import org.hibernate.annotations.Immutable;
import org.hibernate.envers.Audited;

import edu.duke.rs.baseProject.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Immutable
@Audited
public class Role extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "role_id")
	@Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
  @SequenceGenerator(name="role_seq", sequenceName = "ROLE_SEQ", allocationSize = 1)
	private Long id;
	
	@Column(length = 30, unique = true, nullable = false)
	@NonNull
	@Include
	private RoleName name;
}
