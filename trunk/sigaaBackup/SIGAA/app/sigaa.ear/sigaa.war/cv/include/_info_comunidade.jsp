<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="f" uri="http://java.sun.com/jsf/core" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>

<c:if test="${param.ajaxRequest == null and param.dialog == null and sessionScope.ajaxRequest == null}">
<div id="painelInfoComunidade">
	<div id="infoComunidade">
		<h3 class="nomeComunidade"> <h:outputText value="#{comunidadeVirtualMBean.comunidade.nome}" /></h3>
		<p class="descricaoComunidade">	<ufrn:format type="texto" length="300" lineWrap="50" valor="${comunidadeVirtualMBean.comunidade.descricao}" /> </p>
		<a class="mais" href="/sigaa/cv/desc_comunidade.jsf"> Ver descrição completa </a>
		
		<h4> Tipo da Comunidade:</h4>
		<p>
			<h:outputText value="#{comunidadeVirtualMBean.comunidade.tipoComunidadeVirtual.descricao}" />
		</p>
		
		<h4> Administrador: </h4>
		<p class="nomes"> 
			<h:outputText value="#{comunidadeVirtualMBean.comunidade.usuario.pessoa.nomeResumido}" />  
		</p>

		<c:if test="${not empty comunidadeVirtualMBean.moderadores}">		
		<h4> Moderador(es): </h4> 
		<p class="nomes"> 
			<c:forEach var="_membro" items="#{comunidadeVirtualMBean.moderadores}">
				<h:outputText value="#{_membro.pessoa.nomeResumido}" styleClass="nomes" /> &nbsp;  <br/>
			</c:forEach>
		</p>
		</c:if>
		<h:form id="sairComunidade">
		<div style="padding-left:5px;padding-bottom:14px;padding-top:9px;">
			<img src="/sigaa/img/monitoria/businessman_delete.png" style="vertical-align:middle;">
			<h:commandLink  id="sairComunidade" action="#{ comunidadeVirtualMBean.removerPartipanteComunidade }" value="Sair da Comunidade" 
		    	onclick="return(confirm('Você realmente deseja deixar de ser um membro dessa comunidade?'));"/>
		
		</div>
		</h:form>
	</div>

	<div id="listaParticipantes">
		<c:set var="participantes" value="#{membroComunidadeMBean.participantes}" />
		<h3>Participantes </h3>
	
		<div id="fotos">
			<c:forEach var="_membro" items="#{ participantes }" varStatus="loop" end="5">
				<div class="foto">
					<c:if test="${_membro.usuario.idFoto != null}">
						<img src="${ctx}/verFoto?idFoto=${_membro.usuario.idFoto}&key=${ sf:generateArquivoKey(_membro.usuario.idFoto) }" width="60" height="80"  />
					</c:if>
					<c:if test="${_membro.usuario.idFoto == null}">
						<img src="${ctx}/img/no_picture.png" width="60" height="80" />
					</c:if>
					
					<div class="nomeParticipante">
						${_membro.pessoa.nomeResumido}
					</div>
				</div>
			</c:forEach>
		
		<a class="mais" href="/sigaa/cv/MembroComunidade/participantes.jsf"> 
			Ver todos os participantes (${ fn:length(participantes) }) 
		</a>
		</div>
	</div>
</div>
</c:if>