<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style>
#abas-descricao .aba {
	padding: 10px;
}

p.descricao {
	margin: 5px 100px;
	text-align: center;
	font-style: italic;
}
</style>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> &gt; Projeto de Infra-Estrutura em Pesquisa</h2>

	<h:form id="form">
		<h:inputHidden value="#{projetoInfraPesq.confirmButton}" />
		<h:inputHidden value="#{projetoInfraPesq.obj.id}" />

		<table class="formulario">
			<caption>Dados do Projeto</caption>
			
			<tr>
				<th class="obrigatorio">Título: </th>
				<td>
					<h:inputTextarea id="titulo" value="#{projetoInfraPesq.obj.projeto.titulo}" cols="2" style="width: 95%"/>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Ano:</th>
				<td>
					<h:inputText id="ano" value="#{projetoInfraPesq.obj.projeto.ano}" onkeyup="formatarInteiro(this);" maxlength="4" size="4" />
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Tipo:</th>
				<td>
					<h:selectOneMenu id="tipo" value="#{projetoInfraPesq.obj.tipo}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItem itemValue="1" itemLabel="Institucional" />
						<f:selectItem itemValue="2" itemLabel="Externo" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Concedente: </th>
				<td>
					<h:selectOneMenu id="concedente" value="#{projetoInfraPesq.obj.concedente.id}">
						<f:selectItem itemLabel="FINEP" itemValue="14"/>
						<f:selectItems value="#{entidadeFinanciadora.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<th class="obrigatorio">Convenente: </th>
				<td>
					<h:selectOneMenu id="convenente" value="#{projetoInfraPesq.obj.convenente.id}">
						<f:selectItem itemLabel="FUNPEC" itemValue="11"/>
						<f:selectItems value="#{entidadeFinanciadora.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Executora: </th>
				<td>
					<h:selectOneMenu id="unidadeExecutiva" value="#{projetoInfraPesq.obj.executora.id}" style="width: 90%">
						<f:selectItem itemLabel="#{configSistema['nomeInstituicao']}" itemValue="605"/>
						<f:selectItems value="#{unidade.allCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
 
			<tr>
				<th class="obrigatorio">Interveniente: </th>
				<td>
					<h:selectOneMenu id="unidadeInterveniente" value="#{projetoInfraPesq.obj.interveniente.id}" style="width: 90%">
						<f:selectItem itemLabel="PRÓ-REITORIA DE PESQUISA" itemValue="1481"/>
						<f:selectItems value="#{unidade.allCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th class="obrigatorio" width="15%">Coordenador Geral: </th>
				<td>
					<h:inputHidden id="idServidor" value="#{projetoInfraPesq.obj.coordenadorGeral.id}"/>
					<h:inputText id="nomeServidor" value="#{projetoInfraPesq.obj.coordenadorGeral.pessoa.nome}" size="70" onkeyup="CAPS(this);" />
					<ufrn:help>Digite as iniciais do campo, espere o surgimento dos possíveis valores, e selecione o valor desejado que será informado pelo sistema</ufrn:help>
					<ajax:autocomplete source="form:nomeServidor" target="form:idServidor"
						baseUrl="/sigaa/ajaxDocente" className="autocomplete"
						indicator="indicatorDocente" minimumCharacters="3" parameters="tipo=ufrn,situacao=ativo"
						parser="new ResponseXmlToHtmlListParser()" />
					<span id="indicatorDocente" style="display:none; "> 
					<img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> 
					</span>
				</td>
			</tr>
			<c:if test="${not projetoInfraPesq.obj.ativo}">
			<tr>
				<th>Ativo:</th>
				<td><h:selectBooleanCheckbox value="#{projetoInfraPesq.obj.ativo}"/> </td>
			</tr>
			</c:if>
			<tr>
				<td colspan="2"><br /></td>
			</tr>
			<tr>
				<td colspan="2">
				<div id="abas-descricao">
		
					<div id="objetivos-gerais" class="aba">
						<i>Objetivos gerais do Projeto.</i><span class="required"></span><br /><br />
						<h:inputTextarea id="objetivos-gerais" value="#{projetoInfraPesq.obj.projeto.objetivos}" style="width: 95%" rows="10"/>
					</div>
					<div id="beneficiosDiscentes-projeto" class="aba">
						<i>Informe os benefícios esperados no processo ensino-aprendizagem dos alunos de graduação
							e/ou pós-graduação vinculados ao projeto.</i><span class="required"></span><br /><br />
						<h:inputTextarea id="beneficios" value="#{projetoInfraPesq.obj.beneficios}" style="width: 95%" rows="10" />
					</div>
					<div id="retornoAcademico-projeto" class="aba">
						<i>Explicitar o retorno para os cursos de graduação e/ou pós-graduação e para os professores da
							${ configSistema['siglaInstituicao'] } em geral.</i><span class="required"></span><br /><br />			
						<h:inputTextarea id="retorno" value="#{projetoInfraPesq.obj.retorno}" style="width: 95%" rows="10"/>
					</div>
				</div>
				</td>
			</tr>
									
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Cancelar" action="#{projetoInfraPesq.cancelar}" onclick="#{confirm}" immediate="true"/>
						<h:commandButton value="Avançar >>" action="#{projetoInfraPesq.submeterDadosGerais}" /> 
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>

<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('abas-descricao');
        abas.addTab('objetivos-gerais', "Objetivos Gerais");
        abas.addTab('beneficiosDiscentes-projeto', "Benefícios esperados aos discentes");
		abas.addTab('retornoAcademico-projeto', "Retorno aos cursos e aos docentes");
        abas.activate('objetivos-gerais');
    }
};


YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
YAHOO.ext.EventManager.onDocumentReady(Abas2.init, Abas2, true);
</script>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
