<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Avaliação de Projetos de Apoio a Grupo de Pesquisa </h2>
	<a4j:keepAlive beanName="avaliacaoProjetoApoioGrupoPesquisaMBean"/>
	<h:form id="form">
		<table class="formulario" style="width:100%;">
			<caption> Informe os critérios de busca</caption>
			<tbody>
				<tr>
					<td width="2%">
						<h:selectBooleanCheckbox value="#{avaliacaoProjetoApoioGrupoPesquisaMBean.filtroEdital}" styleClass="noborder" id="checkEdital" />
					</td>
					<th  style="text-align: left" width="130px" >Edital: </th>
					<td><h:selectOneMenu value="#{avaliacaoProjetoApoioGrupoPesquisaMBean.idEdital}" style="width: 40%;"  onclick="$('form:checkEdital').checked = true;">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="#{null}"  />
						<f:selectItems value="#{avaliacaoProjetoApoioGrupoPesquisaMBean.editaisDisponiveis}" />
					</h:selectOneMenu></td>
				</tr>
			</tbody>
			<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton  value="Buscar"  action="#{avaliacaoProjetoApoioGrupoPesquisaMBean.buscar}"id="buscar" />
					<h:commandButton action="#{avaliacaoProjetoApoioGrupoPesquisaMBean.cancelar}" value="Cancelar" id="cancelar" onclick="#{confirm}" />
				</td>
			</tr>
			</tfoot>
		</table>
	</h:form>
<c:if test="${not empty avaliacaoProjetoApoioGrupoPesquisaMBean.projetos}">
	<h:form id="listagemResultado">
		<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Avaliar<br/>
			</div>
		</center>

		<table class="formulario" width="100%">
			<caption> Projetos de Apoio a Grupos de Pesquisa (${ fn:length(avaliacaoProjetoApoioGrupoPesquisaMBean.projetos) }) </caption>
			
			<thead>
				<tr>
					<th>Unidade</th>
					<th>Título do Projeto</th>
					<th>Código do Grupo de Pesquisa</th>
					<th>Status</th>
					<th></th>
				</tr>
			</thead>

			<c:forEach items="#{ avaliacaoProjetoApoioGrupoPesquisaMBean.projetos }" var="linha">
				<tr>
					<td> ${ linha.projeto.unidade.sigla } </td>
					<td> ${ linha.projeto.titulo } </td>
					<td> ${ linha.grupoPesquisa.codigo } </td>
					<td> ${ linha.projeto.situacaoProjeto.descricao } </td>
										
					<td width="20">
						<h:commandLink action="#{ avaliacaoProjetoApoioGrupoPesquisaMBean.iniciarCriarAvaliacao}" >
							<h:graphicImage value="/img/seta.gif" style="overflow: visible;" title="Avaliar"/>
							<f:param name="id" value="#{ linha.id }"/>
						</h:commandLink>
					</td>
					
				</tr>				
			</c:forEach>

		</table>
	</h:form>
</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>