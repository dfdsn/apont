package br.com.prox.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.prox.model.DocumentoXml;

@Repository
public interface DocumentoXmlDAO extends JpaRepository<DocumentoXml, Long> {

}
		