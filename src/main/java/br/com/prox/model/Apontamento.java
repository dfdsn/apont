package br.com.prox.model;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;


@Entity
@Data
public class Apontamento implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	@NotEmpty(message = "Título é obrigatório")
	private String titulo;
	
	private String categoria;
	
	@NotNull(message = "Data é obrigatória")
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Temporal(TemporalType.DATE)
	private Date data;
	
//	private Calendar tempoGasto;

	@NotNull(message = "Tempo é obrigatório")
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime  tempoGasto;
	
	@NotNull(message = "Atividade é obrigatória")
	@Enumerated(EnumType.STRING)
	private AtividadeApontamento atividade;
	
	private String descricao;
	
	private String comentario;
	
	@NotNull(message = "Projeto é obrigatório")
	@OneToOne
	private Projeto projeto;
	
	@OneToMany
	private List<Consultor> consultor;
	
	
}