<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> &gt; Avaliação de Projetos de Apoio a Novos Pesquisadores </h2>
	<a4j:keepAlive beanName="avaliacaoProjetoApoioNovosPesquisadoresMBean"/>
	<h:form id = "form">
		<table class="formulario" style="width:100%;">
		<caption> Informe os critérios de busca</caption>
		<tbody>
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{avaliacaoProjetoApoioNovosPesquisadoresMBean.filtroEdital}" styleClass="noborder" id="checkEdital" />
				</td>
				<th  style="text-align: left" width="130px" >Edital: </th>
				<td><h:selectOneMenu value="#{avaliacaoProjetoApoioNovosPesquisadoresMBean.idEdital}" style="width: 40%;"  onclick="$('form:checkEdital').checked = true;">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="#{null}"  />
					<f:selectItems value="#{avaliacaoProjetoApoioNovosPesquisadoresMBean.editaisDisponiveis}" />
				</h:selectOneMenu></td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton  value="Buscar"  action="#{avaliacaoProjetoApoioNovosPesquisadoresMBean.buscar}"id="buscar" />
					<h:commandButton action="#{avaliacaoProjetoApoioNovosPesquisadoresMBean.cancelar}" value="Cancelar" id="cancelar" onclick="#{confirm}" />
				</td>
			</tr>
		</tfoot>
		</table>			
	</h:form>	

<c:if test="${not empty avaliacaoProjetoApoioNovosPesquisadoresMBean.projetos}">
	<h:form id="listagemResultado">
		<center>
			<h:messages/>
			<div class="infoAltRem">
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Avaliar<br/>
			</div>
		</center>
		<table class="formulario" width="100%">
			<caption> Projetos de Apoio a Novos Pesquisadores (${ fn:length(avaliacaoProjetoApoioNovosPesquisadoresMBean.projetos) }) </caption>
			
			<thead>
				<tr>
					<th>Unidade</th>
					<th>Título do Projeto</th>
					<th>Coordenador</th>
					<th>Status</th>
					<th></th>
				</tr>
			</thead>

			<c:forEach items="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.projetos }" var="linha">
				<tr>
					<td> ${ linha.projeto.unidade.sigla } </td>
					<td> ${ linha.projeto.titulo } </td>
					<td> ${ linha.coordenador.pessoa.nome } </td>
					<td> ${ linha.projeto.situacaoProjeto.descricao } </td>
					<td width="20">
						<h:commandLink id="lnkAvaliar" action="#{ avaliacaoProjetoApoioNovosPesquisadoresMBean.iniciarCriarAvaliacao }" >
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