package edu.duke.rs.baseProject.role;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import edu.duke.rs.baseProject.model.BaseEntity;
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
public class Role extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "role_id")
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    @SequenceGenerator(name="role_seq", sequenceName = "ROLE_SEQ", allocationSize = 100)
	private Long id;
	
	@Column(name = "name", length = 20, nullable = false)
	@NonNull
	@Enumerated(EnumType.STRING)
	private RoleName name;
}
