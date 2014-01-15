<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
table.listagem tr.destaque td{
	background: #C8D5EC;
	font-weight: bold;
	padding-left: 20px;
}
</style>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2 class="title"><ufrn:subSistema /> &gt; Busca de Projeto com Avaliações</h2>

	<h:form id="form">
		<table class="formulario" width="85%">
			<caption class="formulario">Busca de Projetos Distribuídos</caption>
			<tr>
				<th class="obrigatorio" style="width: 20%">Edital:</th>
				<td>
					<h:selectOneMenu id="edital" value="#{buscaAvaliacaoProjetoPesquisaMBean.editalPesquisa}" style="width: 80%">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{editalPesquisaMBean.allCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Quantidade de Avaliações:</th>
				<td>
					<h:selectOneMenu id="avaliacoes" value="#{buscaAvaliacaoProjetoPesquisaMBean.quantidadeAvaliacoes}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
						<f:selectItem itemLabel="0" itemValue="0"/>
						<f:selectItem itemLabel="1" itemValue="1"/>
						<f:selectItem itemLabel="2" itemValue="2"/>
						<f:selectItem itemLabel="3" itemValue="3"/>
						<f:selectItem itemLabel="4" itemValue="4"/>
						<f:selectItem itemLabel="5" itemValue="5"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<td align="right"><h:selectBooleanCheckbox value="#{buscaAvaliacaoProjetoPesquisaMBean.formatoRelatorio}"/></td>
				<td align="left"> 
					Formato de Relatório
				</td>
			</tr>			
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Buscar Projetos" action="#{buscaAvaliacaoProjetoPesquisaMBean.buscaAvaliacoesProjeto}" /> 
						<h:commandButton value="Cancelar" action="#{buscaAvaliacaoProjetoPesquisaMBean.cancelar}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
	
	<c:if test="${ not empty buscaAvaliacaoProjetoPesquisaMBean.avaliacoes }">
		<br /><br />
		
		<center>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/> Listar Avaliações
			</div>
		</center>
		
		<table class=listagem id="lista-turmas">
			<caption>Lista dos Projetos Encontrados</caption>
			
			<thead>
				<tr class="header">
					<td> Projeto </td>
					<td> Coordenador </td>
					<td></td>
				</tr>
			</thead>
				
			<c:set var="_unidade" />
			<c:forEach items="#{buscaAvaliacaoProjetoPesquisaMBean.avaliacoes}" var="item" varStatus="s">
				
				<c:set var="unidadeAtual" value="${item.projetoPesquisa.projeto.unidade.nome}"/>
				 <c:if test="${ _unidade != unidadeAtual }">
					<tr class="destaque">
						<td colspan="17" style="font-variant: small-caps;" style="text-align: left;">
							${item.projetoPesquisa.projeto.unidade.nome}
						</td>
					</tr>
				 </c:if>
				
				<tr class="${s.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" style="font-size: xx-small">
					<td>${ item.projetoPesquisa.codigoTitulo }</td>
					<td>${ item.projetoPesquisa.projeto.coordenador.pessoa.nome }</td>
					<td>
						<html:link action="/pesquisa/avaliarProjetoPesquisa?dispatch=resumoAvaliacoes&obj.projetoPesquisa.id=${item.projetoPesquisa.id}">
							<img src="${ctx}/img/listar.gif"
								alt="Listar Avaliações"
								title="Listar Avaliações" />
						</html:link>
					</td>
				</tr>
				
				<c:set var="_unidade" value="${ unidadeAtual }"/>
			</c:forEach>

		</table>
		
	</c:if>
	
	</h:form>
	<br />
	
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>