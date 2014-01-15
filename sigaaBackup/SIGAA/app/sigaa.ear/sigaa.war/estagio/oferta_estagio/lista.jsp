<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:form id="form">
<c:if test="${not ofertaEstagioMBean.portalDiscente}">
	<h2> <ufrn:subSistema /> &gt; Consultar Ofertas de Estágio</h2>	
	<div class="descricaoOperacao">
		<p><b>Caro ${ofertaEstagioMBean.portalCoordenadorGraduacao ? 'Coordenador' : 'Usuário' },</b></p><br/>
		<p>Nesta tela você poderá buscar <b>Ofertas de Estágio</b> cadastradas, combinando os filtros para refinar sua consulta.
		Podendo também <b>Visualizar</b> ${ofertaEstagioMBean.portalCoordenadorGraduacao ? 'ou <b>Alterar</b>' : ''}  as informações cadastradas.</p>
	</div>
</c:if>
<c:if test="${ofertaEstagioMBean.portalDiscente}">
	<h2> <ufrn:subSistema /> &gt; Mural de Vagas</h2>
	<div class="descricaoOperacao">
		<p><b>Caro Discente,</b></p><br/>
		<p>Nesta tela serão exibidas as <b>Ofertas de Estágio</b> abertas e disponíveis para o seu <b>Curso</b>.</p>
	</div>		
</c:if>

<a4j:keepAlive beanName="ofertaEstagioMBean" />
	<c:if test="${not ofertaEstagioMBean.portalDiscente}">
		<%@include file="include/_busca.jsp"%>
	</c:if>
	
	<c:if test="${not empty ofertaEstagioMBean.listaOfertas}">
		<div class="infoAltRem">													
			<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Oferta de Estágio
			<c:if test="${ofertaEstagioMBean.portalCoordenadorGraduacao || convenioEstagioMBean.permiteAnalisarConvenio}">
				<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>: Inscrever Discente em Processo Seletivo
			</c:if>
			<c:if test="${ofertaEstagioMBean.portalDiscente}">
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Inscrever-se em Processo Seletivo
			</c:if>
			<c:if test="${not ofertaEstagioMBean.portalDiscente && not ofertaEstagioMBean.portalDocente}">
				<h:graphicImage value="/img/estagio/view_interesse.png" style="overflow: visible;"/>: Visualizar Inscrições
				<br/><h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar Oferta de Estágio
			</c:if>		
			<c:if test="${ofertaEstagioMBean.portalCoordenadorGraduacao || convenioEstagioMBean.permiteAnalisarConvenio}">
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Analisar Oferta de Estágio
			</c:if>					
		</div>
	</c:if>
	
	<c:set var="ofertas" value="#{ofertaEstagioMBean.listaOfertas}"/>
	<c:set var="nomeCaption" value="Ofertas de Estágio Encontradas "/>
	<c:if test="${not empty ofertas}">
		<%@include file="include/_lista.jsp"%>	
	</c:if>		
	<c:if test="${empty ofertas}">
		<table class="listagem" style="width: 100%">
			<caption class="listagem">${nomeCaption}</caption>
				<tr>
					<td colspan="9" style="text-align: center;">
						<i>Nenhuma Oferta de Estágio encontrada.</i>
					</td>
				</tr>
		</table>
	</c:if>	
	<c:if test="${ofertaEstagioMBean.portalDiscente}">
	<c:set var="ofertas" value="#{ofertaEstagioMBean.outrasOfertas}"/>
	<c:set var="nomeCaption" value="Ofertas de Estágio de Outros Cursos"/>
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