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
import br.com.prox.nfe.util.ObjetoUtil;
import br.com.prox.nfe.util.WebServiceUtil;
import br.com.prox.nfe.util.XmlUtil;
import br.inf.portalfiscal.nfe.consreci.TConsReciNFe;
import br.inf.portalfiscal.nfe.consreci.TRetConsReciNFe;
import webservice.nfe.NfeRetAutorizacaoStub;


/**
 * Classe Responsavel Por pegar o Retorno da NFE, apos o Envio.
 * 
 * @author Samuel Oliveira
 */
public class ConsultaRecibo {

	private static NfeRetAutorizacaoStub.NfeRetAutorizacaoLoteResult result;
	private static ConfiguracoesIniciaisNfe configuracoesNfe;
	private static CertificadoUtil certUtil;


	/**
	 * Metodo Responsavel Por Pegar o Xml De Retorno.
	 * 
	 * @param tConsReciNFe
	 * @param valida
	 * @param tipo
	 * @return
	 * @throws NfeException
	 */
	
	public static TRetConsReciNFe reciboNfe(TConsReciNFe tConsReciNFe, boolean valida, String tipo) throws NfeException {

		try {
			
			certUtil = new CertificadoUtil();
			configuracoesNfe = ConfiguracoesIniciaisNfe.getInstance();
			boolean nfce = tipo.equals(ConstantesUtil.NFCE);

			/**
			 * Informaçoes do Certificado Digital.
			 */
			certUtil.iniciaConfiguracoes();

			String xml = XmlUtil.objectToXml(tConsReciNFe);
		
			if(valida){
				String erros = Validar.validaXml(xml, Validar.CONSULTA_RECIBO);
				if(!ObjetoUtil.isEmpty(erros)){
					throw new NfeValidacaoException("Erro Na Validação do Xml: "+erros);
				}
			}
			
			OMElement ome = AXIOMUtil.stringToOM(xml.toString());
			NfeRetAutorizacaoStub.NfeDadosMsg dadosMsg = new NfeRetAutorizacaoStub.NfeDadosMsg();
			dadosMsg.setExtraElement(ome);

			NfeRetAutorizacaoStub.NfeCabecMsg nfeCabecMsg = new NfeRetAutorizacaoStub.NfeCabecMsg();
			/**
			 * Codigo do Estado.
			 */
			nfeCabecMsg.setCUF(String.valueOf(configuracoesNfe.getEstado().getCodigoIbge()));

			/**
			 * Versao do XML
			 */
			nfeCabecMsg.setVersaoDados(configuracoesNfe.getVersaoNfe());

			NfeRetAutorizacaoStub.NfeCabecMsgE nfeCabecMsgE = new NfeRetAutorizacaoStub.NfeCabecMsgE();
			nfeCabecMsgE.setNfeCabecMsg(nfeCabecMsg);

			NfeRetAutorizacaoStub stub = new NfeRetAutorizacaoStub(nfce ? WebServiceUtil.getUrl(ConstantesUtil.NFCE, ConstantesUtil.SERVICOS.CONSULTA_RECIBO) : WebServiceUtil.getUrl(ConstantesUtil.NFE, ConstantesUtil.SERVICOS.CONSULTA_RECIBO));
			result = stub.nfeRetAutorizacaoLote(dadosMsg, nfeCabecMsgE);

			return XmlUtil.xmlToObject(result.getExtraElement().toString(), TRetConsReciNFe.class);
			
		} catch (RemoteException | XMLStreamException | JAXBException e) {
			throw new NfeException(e.getMessage());
		}
		
	}
}