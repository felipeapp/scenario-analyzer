<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>

<h2><ufrn:subSistema /> &gt; Relatório Individual do Docente (RID) para Progressão Funcional </h2>

<div class="descricaoOperacao">
	<p>
		Caro professor,
	</p>
	<p>
		Este relatório traz informações sobre todas as suas atividades no formato requerido pela
		Resolução nº 186/93-CONSEPE para progressão funcional. Por favor, confira todas as informações
		nele constantes. 
	</p>
	<p>
		Caso note a ausência do período na informação do semestre ou o semestre apareça como indefinido, é necessário atualizar a informação da data da publicação 
		correspondente na sua produção intelectual para que o relatório possa exibir o semestre corretamente.
	</p>
	<p>
		Nos itens em que não constar a pontuação, a comissão responsável irá analisar para conferir a pontuação
		correta, ajustando os totais devidamente.
	</p>
</div>

<h:form id="form">
<table class="formulario" width="40%">
<caption> Critérios para emissão </caption>

	<ufrn:subSistema teste="pesquisa, portalPlanejamento">
	<c:if test="${acesso.prodocente or acesso.pesquisa or acesso.planejamento}">
	<tr>
		<th>Docente: </th>
		<td>
			<h:inputHidden id="id" value="#{relatorioRID.servidor.id}"></h:inputHidden>
			<h:inputText id="nome" value="#{relatorioRID.servidor.pessoa.nome}" size="60" />

			<ajax:autocomplete
					source="form:nome" target="form:id"
					baseUrl="/sigaa/ajaxServidor" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
					parser="new ResponseXmlToHtmlListParser()" /> <span id="indicator"
					style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
		</td>
	</tr>
	</c:if>
	</ufrn:subSistema>

	<tr>
		<th class="required"> Ano-Período Inicial: </th>
		<td>
			<h:inputText id="anoInicial" value="#{relatorioRID.anoInicial}" size="4" maxlength="4" onkeyup="formatarInteiro(this);" label="Ano-Período Inicial" required="true"/>-
			<h:selectOneMenu id="periodoInicial" value="#{relatorioRID.periodoInicial}" style="width: 40px">
				<f:selectItem itemLabel="1" itemValue="1"/>
				<f:selectItem itemLabel="2" itemValue="2"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th class="required"> Ano-Período Final: </th>
		<td>
			<h:inputText id="anoFinal" value="#{relatorioRID.anoFinal}" size="4" maxlength="4" onkeyup="formatarInteiro(this);" label="Ano-Período Final" required="true"/>-
			<h:selectOneMenu id="periodoFinal" value="#{relatorioRID.periodoFinal}" style="width: 40px">
				<f:selectItem itemLabel="1" itemValue="1"/>
				<f:selectItem itemLabel="2" itemValue="2"/>
			</h:selectOneMenu>
		</td>
	</tr>
	
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatorioRID.emiteRelatorio}" value="Emitir Relatório"/>
		</td>
	</tr>
	</tfoot>
</table>
</h:form>
	<br/>
	<div class="obrigatorio" style="width:100%"> Campos de preenchimento obrigatório.</div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>