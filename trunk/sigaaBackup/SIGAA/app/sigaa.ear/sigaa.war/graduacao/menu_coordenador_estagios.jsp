<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>
<ufrn:subSistema teste="portalCoordenadorGrad">
<h:form id="menu_coordenador">
<div id="menu-dropdown">
<input type="hidden" id="jscook_action" name="jscook_action" />
<t:jscookMenu layout="hbr" theme="ThemeOffice" styleLocation="/css/jscookmenu">
	<t:navigationMenuItem id="estagio" itemLabel="Estágio" icon="/img/estagio/estagio_menu.png">
		<t:navigationMenuItem id="convenios" itemLabel="Convênio de Estágio" >
			<t:navigationMenuItem id="cadConvenioEstagio" itemLabel="Solicitar Convênio de Estágio" action="#{ convenioEstagioMBean.iniciar }"/>
			<t:navigationMenuItem id="consultarConvenioEstagio" itemLabel="Consultar Convênio de Estágio" action="#{ convenioEstagioMBean.iniciarConsulta }"/>		
		</t:navigationMenuItem>
		<t:navigationMenuItem id="ofertaEstagio" itemLabel="Oferta de Estágio" >
			<t:navigationMenuItem id="cadOfertaEstagio" itemLabel="Cadastrar Oferta de Estágio" action="#{ ofertaEstagioMBean.iniciar }"/>
			<t:navigationMenuItem id="consultarOfertaEstagio" itemLabel="Consultar Oferta de Estágio" action="#{ ofertaEstagioMBean.iniciarConsulta }"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem id="gerenciarEstagios" itemLabel="Gerenciar Estagiários" action="#{buscaEstagioMBean.iniciar}" split="true"/>		
		<t:navigationMenuItem id="cadastrarEstagios" itemLabel="Cadastrar Estagiários Avulso" action="#{estagioMBean.iniciarCadastroAvulso}"/>				
	</t:navigationMenuItem>	
</t:jscookMenu>

</div>
</h:form>

</ufrn:subSistema>
<script>
function redirectManual(){
	return window.open("${linkPublico.urlDownload}/manual_nee_solicitacao_apoio.pdf","_blank");
}
</script>