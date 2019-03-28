package edu.duke.rs.baseProject.user;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
public class User extends BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prooduct_id_seq")
    @SequenceGenerator(name="prooduct_id_seq", sequenceName = "PRODUCT_ID_SEQ", allocationSize = 100)
	private Long id;
	
	@Column(name = "user_name", length = 30, nullable = false)
	@NonNull
	@NotBlank
	@Size(min = 8, max = 30)
	private String userName;
}
