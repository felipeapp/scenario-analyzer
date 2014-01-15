<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:form id="form">
<c:if test="${not ofertaEstagioMBean.portalDiscente}">
	<h2> <ufrn:subSistema /> &gt; Consultar Ofertas de Est�gio</h2>	
	<div class="descricaoOperacao">
		<p><b>Caro ${ofertaEstagioMBean.portalCoordenadorGraduacao ? 'Coordenador' : 'Usu�rio' },</b></p><br/>
		<p>Nesta tela voc� poder� buscar <b>Ofertas de Est�gio</b> cadastradas, combinando os filtros para refinar sua consulta.
		Podendo tamb�m <b>Visualizar</b> ${ofertaEstagioMBean.portalCoordenadorGraduacao ? 'ou <b>Alterar</b>' : ''}  as informa��es cadastradas.</p>
	</div>
</c:if>
<c:if test="${ofertaEstagioMBean.portalDiscente}">
	<h2> <ufrn:subSistema /> &gt; Mural de Vagas</h2>
	<div class="descricaoOperacao">
		<p><b>Caro Discente,</b></p><br/>
		<p>Nesta tela ser�o exibidas as <b>Ofertas de Est�gio</b> abertas e dispon�veis para o seu <b>Curso</b>.</p>
	</div>		
</c:if>

<a4j:keepAlive beanName="ofertaEstagioMBean" />
	<c:if test="${not ofertaEstagioMBean.portalDiscente}">
		<%@include file="include/_busca.jsp"%>
	</c:if>
	
	<c:if test="${not empty ofertaEstagioMBean.listaOfertas}">
		<div class="infoAltRem">													
			<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Oferta de Est�gio
			<c:if test="${ofertaEstagioMBean.portalCoordenadorGraduacao || convenioEstagioMBean.permiteAnalisarConvenio}">
				<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>: Inscrever Discente em Processo Seletivo
			</c:if>
			<c:if test="${ofertaEstagioMBean.portalDiscente}">
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Inscrever-se em Processo Seletivo
			</c:if>
			<c:if test="${not ofertaEstagioMBean.portalDiscente && not ofertaEstagioMBean.portalDocente}">
				<h:graphicImage value="/img/estagio/view_interesse.png" style="overflow: visible;"/>: Visualizar Inscri��es
				<br/><h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Oferta de Est�gio
			</c:if>		
			<c:if test="${ofertaEstagioMBean.portalCoordenadorGraduacao || convenioEstagioMBean.permiteAnalisarConvenio}">
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Analisar Oferta de Est�gio
			</c:if>					
		</div>
	</c:if>
	
	<c:set var="ofertas" value="#{ofertaEstagioMBean.listaOfertas}"/>
	<c:set var="nomeCaption" value="Ofertas de Est�gio Encontradas "/>
	<c:if test="${not empty ofertas}">
		<%@include file="include/_lista.jsp"%>	
	</c:if>		
	<c:if test="${empty ofertas}">
		<table class="listagem" style="width: 100%">
			<caption class="listagem">${nomeCaption}</caption>
				<tr>
					<td colspan="9" style="text-align: center;">
						<i>Nenhuma Oferta de Est�gio encontrada.</i>
					</td>
				</tr>
		</table>
	</c:if>	
	<c:if test="${ofertaEstagioMBean.portalDiscente}">
	<c:set var="ofertas" value="#{ofertaEstagioMBean.outrasOfertas}"/>
	<c:set var="nomeCaption" value="Ofertas de Est�gio de Outros Cursos"/>
	<c:if test="${not empty ofertas}">
		<%@include file="include/_lista.jsp"%>	
	</c:if>
		<table  class="listagem" style="width: 100%">
			<tfoot>
				<tr>
					<td colspan="7" align="center">
						<h:commandButton value="Cancelar" action="#{ofertaEstagioMBean.cancelar}" onclick="#{confirm}" id="btCancel"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</c:if>				
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>