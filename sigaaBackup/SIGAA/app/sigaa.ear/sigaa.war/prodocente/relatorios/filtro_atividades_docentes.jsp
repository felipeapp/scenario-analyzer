<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>

<h2><ufrn:subSistema /> &gt; Relat�rio Individual do Docente (RID) para Progress�o Funcional </h2>

<div class="descricaoOperacao">
	<p>
		Caro professor,
	</p>
	<p>
		Este relat�rio traz informa��es sobre todas as suas atividades no formato requerido pela
		Resolu��o n� 186/93-CONSEPE para progress�o funcional. Por favor, confira todas as informa��es
		nele constantes. 
	</p>
	<p>
		Caso note a aus�ncia do per�odo na informa��o do semestre ou o semestre apare�a como indefinido, � necess�rio atualizar a informa��o da data da publica��o 
		correspondente na sua produ��o intelectual para que o relat�rio possa exibir o semestre corretamente.
	</p>
	<p>
		Nos itens em que n�o constar a pontua��o, a comiss�o respons�vel ir� analisar para conferir a pontua��o
		correta, ajustando os totais devidamente.
	</p>
</div>

<h:form id="form">
<table class="formulario" width="40%">
<caption> Crit�rios para emiss�o </caption>

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
		<th class="required"> Ano-Per�odo Inicial: </th>
		<td>
			<h:inputText id="anoInicial" value="#{relatorioRID.anoInicial}" size="4" maxlength="4" onkeyup="formatarInteiro(this);" label="Ano-Per�odo Inicial" required="true"/>-
			<h:selectOneMenu id="periodoInicial" value="#{relatorioRID.periodoInicial}" style="width: 40px">
				<f:selectItem itemLabel="1" itemValue="1"/>
				<f:selectItem itemLabel="2" itemValue="2"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th class="required"> Ano-Per�odo Final: </th>
		<td>
			<h:inputText id="anoFinal" value="#{relatorioRID.anoFinal}" size="4" maxlength="4" onkeyup="formatarInteiro(this);" label="Ano-Per�odo Final" required="true"/>-
			<h:selectOneMenu id="periodoFinal" value="#{relatorioRID.periodoFinal}" style="width: 40px">
				<f:selectItem itemLabel="1" itemValue="1"/>
				<f:selectItem itemLabel="2" itemValue="2"/>
			</h:selectOneMenu>
		</td>
	</tr>
	
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatorioRID.emiteRelatorio}" value="Emitir Relat�rio"/>
		</td>
	</tr>
	</tfoot>
</table>
</h:form>
	<br/>
	<div class="obrigatorio" style="width:100%"> Campos de preenchimento obrigat�rio.</div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>