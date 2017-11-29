package br.com.prox.nfe;

import java.rmi.RemoteException;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;

import br.com.prox.nfe.exception.NfeException;
import br.com.prox.nfe.exception.NfeValidacaoException;
import br.com.prox.nfe.util.CertificadoUtil;
import br.com.prox.nfe.util.ConstantesUtil;
import br.com.prox.nfe.util.Estados;
import br.com.prox.nfe.util.ObjetoUtil;
import br.com.prox.nfe.util.WebServiceUtil;
import br.com.prox.nfe.util.XmlUtil;
import br.inf.portalfiscal.nfe.conssit.TConsSitNFe;
import br.inf.portalfiscal.nfe.conssit.TRetConsSitNFe;
import webservice.nfe.NfeConsulta2Stub;
import webservice.nfe.NfeConsultaStub;

/**
 * Classe responsavel por Consultar a Situaçao do XML na SEFAZ.
 * 
 * @author Samuel Oliveira - samuk.exe@hotmail.com - www.samuelweb.com.br
 * 
 */

public class ConsultaXml {

	private static NfeConsulta2Stub.NfeConsultaNF2Result result;
	private static NfeConsultaStub.NfeConsultaNFResult resultBA;
	private static ConfiguracoesIniciaisNfe configuracoesNfe;
	private static CertificadoUtil certUtil;

	/**
	 * Classe Reponsavel Por Consultar o status da NFE na SEFAZ
	 * 
	 * @param consSitNFe
	 * @param valida
	 * @param tipo
	 * @return
	 * @throws NfeException
	 */
	public static TRetConsSitNFe consultaXml(TConsSitNFe consSitNFe, boolean valida, String tipo) throws NfeException {
		
		certUtil = new CertificadoUtil();
		configuracoesNfe = ConfiguracoesIniciaisNfe.getInstance();
		boolean nfce = tipo.equals(ConstantesUtil.NFCE);
		boolean BA = configuracoesNfe.getEstado().equals(Estados.BA);

		try {

			certUtil.iniciaConfiguracoes();
			
			String xml = XmlUtil.objectToXml(consSitNFe);
			
			if(valida){
				String erros = Validar.validaXml(xml, Validar.CONSULTA_XML);
				if(!ObjetoUtil.isEmpty(erros)){
					throw new NfeValidacaoException("Erro Na Validação do Xml: "+erros);
				}
			}
			
			System.out.println("Xml Consulta: "+xml);
			OMElement ome = AXIOMUtil.stringToOM(xml);
			
			if(BA && !nfce){
				NfeConsultaStub.NfeDadosMsg dadosMsgBA = new NfeConsultaStub.NfeDadosMsg();
				dadosMsgBA.setExtraElement(ome);
				
				NfeConsultaStub.NfeCabecMsg nfeCabecMsgBA = new NfeConsultaStub.NfeCabecMsg();
				nfeCabecMsgBA.setCUF(String.valueOf(configuracoesNfe.getEstado().getCodigoIbge()));
				nfeCabecMsgBA.setVersaoDados(configuracoesNfe.getVersaoNfe());
				
				NfeConsultaStub.NfeCabecMsgE nfeCabecMsgEBA = new NfeConsultaStub.NfeCabecMsgE();
				nfeCabecMsgEBA.setNfeCabecMsg(nfeCabecMsgBA);
				
				NfeConsultaStub stubBA = new NfeConsultaStub(nfce ? WebServiceUtil.getUrl(ConstantesUtil.NFCE, ConstantesUtil.SERVICOS.CONSULTA_XML) : WebServiceUtil.getUrl(ConstantesUtil.NFE, ConstantesUtil.SERVICOS.CONSULTA_XML));
				resultBA = stubBA.nfeConsultaNF(dadosMsgBA, nfeCabecMsgEBA);
				
				return XmlUtil.xmlToObject(resultBA.getExtraElement().toString(), TRetConsSitNFe.class);
			}else{
				NfeConsulta2Stub.NfeDadosMsg dadosMsg = new NfeConsulta2Stub.NfeDadosMsg();
				dadosMsg.setExtraElement(ome);
				
				NfeConsulta2Stub.NfeCabecMsg nfeCabecMsg = new NfeConsulta2Stub.NfeCabecMsg();
				nfeCabecMsg.setCUF(String.valueOf(configuracoesNfe.getEstado().getCodigoIbge()));
				nfeCabecMsg.setVersaoDados(configuracoesNfe.getVersaoNfe());
				
				NfeConsulta2Stub.NfeCabecMsgE nfeCabecMsgE = new NfeConsulta2Stub.NfeCabecMsgE();
				nfeCabecMsgE.setNfeCabecMsg(nfeCabecMsg);
				
				NfeConsulta2Stub stub = new NfeConsulta2Stub(nfce ? WebServiceUtil.getUrl(ConstantesUtil.NFCE, ConstantesUtil.SERVICOS.CONSULTA_XML) : WebServiceUtil.getUrl(ConstantesUtil.NFE, ConstantesUtil.SERVICOS.CONSULTA_XML));
				result = stub.nfeConsultaNF2(dadosMsg, nfeCabecMsgE);
				
				return XmlUtil.xmlToObject(result.getExtraElement().toString(), TRetConsSitNFe.class);
			}
			
		} catch (RemoteException | XMLStreamException | JAXBException e) {
			throw new NfeException(e.getMessage());
		}
		
	}

}
