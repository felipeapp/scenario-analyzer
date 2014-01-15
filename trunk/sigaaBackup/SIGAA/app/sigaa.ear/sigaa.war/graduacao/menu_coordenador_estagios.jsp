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
	<t:navigationMenuItem id="estagio" itemLabel="Est�gio" icon="/img/estagio/estagio_menu.png">
		<t:navigationMenuItem id="convenios" itemLabel="Conv�nio de Est�gio" >
			<t:navigationMenuItem id="cadConvenioEstagio" itemLabel="Solicitar Conv�nio de Est�gio" action="#{ convenioEstagioMBean.iniciar }"/>
			<t:navigationMenuItem id="consultarConvenioEstagio" itemLabel="Consultar Conv�nio de Est�gio" action="#{ convenioEstagioMBean.iniciarConsulta }"/>		
		</t:navigationMenuItem>
		<t:navigationMenuItem id="ofertaEstagio" itemLabel="Oferta de Est�gio" >
			<t:navigationMenuItem id="cadOfertaEstagio" itemLabel="Cadastrar Oferta de Est�gio" action="#{ ofertaEstagioMBean.iniciar }"/>
			<t:navigationMenuItem id="consultarOfertaEstagio" itemLabel="Consultar Oferta de Est�gio" action="#{ ofertaEstagioMBean.iniciarConsulta }"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem id="gerenciarEstagios" itemLabel="Gerenciar Estagi�rios" action="#{buscaEstagioMBean.iniciar}" split="true"/>		
		<t:navigationMenuItem id="cadastrarEstagios" itemLabel="Cadastrar Estagi�rios Avulso" action="#{estagioMBean.iniciarCadastroAvulso}"/>				
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