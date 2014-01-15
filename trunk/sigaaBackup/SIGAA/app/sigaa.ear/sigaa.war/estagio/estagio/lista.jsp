<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="buscaEstagioMBean"/>
	<h2> <ufrn:subSistema /> &gt; Gerenciar Estágios</h2>
	<div class="descricaoOperacao">
		<c:if test="${ofertaEstagioMBean.portalCoordenadorGraduacao}">
			<p><b>Caro Coordenador,</b></p><br/>		
			<p>Através dessa tela é possível gerenciar todos os Estagiários cadastrados, podendo Visualizar, Alterar e Analisar.</p>
		</c:if>
		<c:if test="${ofertaEstagioMBean.portalConcedenteEstagio || ofertaEstagioMBean.portalDocente}">
			<p><b>Caro Usuário,</b></p><br/>
			<c:if test="${ofertaEstagioMBean.portalConcedenteEstagio}">
				<p>Através dessa tela é possível gerenciar todos os Estagiários cadastrados, podendo Consultar, Visualizar e/ou Cancelar a qualquer momento.</p>
			</c:if>
		</c:if>
		<c:if test="${!ofertaEstagioMBean.portalDiscente}">
			<p>A Consulta pode ser realizada pela matrícula ou nome do Discente, pelo CNPJ ou nome do Concedente do Estágio,
			Orientador, pelo Tipo de Estágio, pelo Período do Estágio e pelo Curso. Podendo ser combinados todos os filtros.</p>
		</c:if>
		<c:if test="${ofertaEstagioMBean.portalDiscente}">
			<p><b>Caro Discente,</b></p><br/>
			<p>Através dessa tela é possível Consultar todos os Estágios cadastrados.</p>
		</c:if>
	</div>

<h:form id="form">

	<c:if test="${!buscaEstagioMBean.portalDiscente}">
		<%@include file="include/_busca.jsp"%>
	</c:if>
	
	<c:if test="${not empty buscaEstagioMBean.listaEstagiosPendentes 
			   || not empty buscaEstagioMBean.listaEstagiosCancelamento 
			   || not empty buscaEstagioMBean.listaEstagios}">
		<center>
			<div class="infoAltRem">
				<c:if test="${not empty buscaEstagioMBean.listaEstagiosPendentes && estagioMBean.permiteAnalisarEstagio}">
					<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Analisar Estagiário
				</c:if>
				<h:graphicImage value="/img/biblioteca/emprestimos_ativos.png" style="overflow: visible;" />: Visualizar Menu
			</div>
		</center>
	</c:if>
	
	<c:set var="nenhum" value="${true}" />
	<c:if test="${not empty buscaEstagioMBean.listaEstagiosPendentes}">
	<c:set var="estagios" value="#{buscaEstagioMBean.listaEstagiosPendentes}"/>	
	<c:set var="nomeCaption" value="Estagiários Pendentes de Análise "/>
	<c:set var="pendentes" value="${true}"/>
		<%@include file="include/_lista.jsp"%>
	<c:set var="nenhum" value="${false}" />
	</c:if>		
	
	<c:if test="${not empty buscaEstagioMBean.listaEstagiosCancelamento}">
	<c:set var="estagios" value="#{buscaEstagioMBean.listaEstagiosCancelamento}"/>
	<c:set var="nomeCaption" value="Estágios com Cancelamento Solicitado"/>
		<c:set var="pendentes" value="${false}"/>
		<%@include file="include/_lista.jsp"%>	
	<c:set var="nenhum" value="${false}" />
	</c:if>		
		
	<c:if test="${not empty buscaEstagioMBean.listaEstagios}">
	<c:set var="estagios" value="#{buscaEstagioMBean.listaEstagios}"/>
	<c:set var="nomeCaption" value="Estagiários Encontrados "/>
		<c:set var="pendentes" value="${false}"/>
		<%@include file="include/_lista.jsp"%>	
	<c:set var="nenhum" value="${false}" />
	</c:if>
			
	<c:if test="${nenhum}">
		<table class="listagem" style="width: 100%">
				<tr>
					<td colspan="9" style="text-align: center;">
						<i>Nenhum Estagio encontrado.</i>
					</td>
				</tr>
		</table>
	</c:if>	
</h:form>	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>