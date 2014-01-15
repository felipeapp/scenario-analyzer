<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
.scrollbar {
  	margin-left: 155px;  	
	width: 98%;
	overflow:auto;
}

.rich-tab-bottom-line table tr td {
	padding: 0px;
	font-size: 13px;
	margin: 3px;
}

</style>

<script type=text/javascript>  

	/* Limita o campo ao limite de um inteiro */
	function limiteInteiro(campo){
		var maxNum = 2147483647;
		if(campo.value > maxNum){
			campo.value = campo.value.slice(0, -1);
		}
		return campo;
	}
	
	var Abas = function() {
		return {
		    init : function(){
		        var abas = new YAHOO.ext.TabPanel('operacoes-relatorio');
			        abas.addTab('objetivos', "Objetivos");
					abas.addTab('avaliacao', "Avaliação")
					abas.addTab('participantes', "Participantes")
					abas.addTab('arquivos', "Arquivos")
	    	    <c:if test="${sessionScope.aba != null}">
		    		abas.activate('${sessionScope.aba}');
		    	</c:if>
		        <c:if test="${empty sessionScope.aba}">
		        	abas.activate('avaliacao');
		   	 	</c:if>
		    }
	    }
	}();
	YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
	function setAba(aba) {
		document.getElementById('aba').value = aba;
	}

	J = jQuery.noConflict();
	function habilitarDetalhes(idSolicitacao) {
		var linha = idSolicitacao;
		if ( J('#tbAtividades_'+linha).css('display') == 'none' ) {
			if (/msie/.test( navigator.userAgent.toLowerCase() ))
				J('#tbAtividades_'+linha).css('display', 'inline-block');
			else
				J('#tbAtividades_'+linha).css('display', '');			
		} else {
			J('#tbAtividades_'+linha).css('display', 'none');		
		}
	}

</script>

<f:view>
<h:outputText value="#{relatorioProjeto.create}"/>
<h:outputText value="#{participanteAcaoExtensao.create}"/>

	<h2><ufrn:subSistema /> &gt; Relatório de Projetos de Extensão</h2>

	<h:form id="form" enctype="multipart/form-data" prependId="false">
		<input type="hidden" name="aba" id="aba" />
		
		<table class="formulario" width="100%">
		<caption class="listagem">CADASTRO DE ${relatorioAcaoExtensao.mbean.obj.tipoRelatorio.descricao} DE PROJETOS DE EXTENSÃO</caption>

		<tr>
				<th width="25%"><b>Código do Projeto:</b></th>
				<td><h:outputText value="#{relatorioAcaoExtensao.mbean.obj.atividade.codigo}" /></td>
		</tr>

		<tr>
				<th><b>Título do Projeto:</b></th>
				<td><h:outputText value="#{relatorioAcaoExtensao.mbean.obj.atividade.titulo}"/></td>
		</tr>
		
		<tr>
				<th><b>Unidade Proponente:</b></th>
				<td><h:outputText value="#{relatorioAcaoExtensao.mbean.obj.atividade.unidade.nome}" /></td>
		</tr>
		
		<tr>
				<th><b>Fontes Financiamento:</b></th>
				<td><h:outputText value="#{relatorioAcaoExtensao.mbean.obj.atividade.fonteFinanciamentoString}" /></td>
		</tr>

		<tr>
				<th>
					<b>Nº Discentes Envolvidos:</b>					
				</th>
				<td>
					<h:outputText value="#{relatorioAcaoExtensao.mbean.obj.atividade.totalDiscentes}" />
					&nbsp;
					<ufrn:help img="/img/ajuda.gif">Total e discentes envolvidos na execução do Projeto.</ufrn:help>
				</td>
		</tr>
		
		<tr>
			<th class="obrigatorio"><b>Esta Ação foi realizada:</b></th>
			<td>
				<h:selectOneRadio value="#{ relatorioAcaoExtensao.mbean.obj.acaoRealizada }" id="acaoFoiRealizada">
					<f:selectItem itemLabel="SIM" itemValue="TRUE" />
					<f:selectItem itemLabel="NÃO" itemValue="FALSE" />
					<a4j:support event="onclick" reRender="realizacaoAcao" onsubmit="true" />
				</h:selectOneRadio>
			</td>
		</tr>
		
		<tr>
			<td colspan="2">
				 <rich:tabPanel headerAlignment="left" switchType="client" id="tab" selectedTab="abaMeusObjetivos"> 
       				<rich:tab label="Objetivos" name="abaMeusObjetivos">
       					<%@include file="include/objetivos.jsp"%>
       				</rich:tab>
       				<rich:tab label="Avaliação" name="abaMinhasAvaliacoes">
       					<%@include file="include/avaliacao.jsp"%>
       				</rich:tab>
       				<rich:tab label="Participantes" name="abaMeusParticipantes">
       					<%@include file="include/participantes.jsp"%>
       				</rich:tab>
       				<rich:tab label="Arquivos" name="abaMeusArquivos">
       					<%@include file="include/arquivos.jsp"%>
       				</rich:tab>
     			</rich:tabPanel>
			</td>
		</tr>

		<tfoot>
			<tr>
				<td colspan="2">
						<h:commandButton immediate="true" value="Importar Dados do Relatório Parcial" action="#{relatorioAcaoExtensao.mbean.importarDadosRelatorioParcial}" 
							rendered="#{ relatorioProjeto.exibeBotaoImportar }" />
						<h:commandButton value="Salvar (Rascunho)" action="#{relatorioAcaoExtensao.mbean.salvar}" rendered="#{!relatorioAcaoExtensao.mbean.readOnly}" onclick="this.form.target='_self';"/>	
						<h:commandButton value="#{relatorioAcaoExtensao.mbean.confirmButton}" action="#{relatorioAcaoExtensao.mbean.enviar}" rendered="#{!relatorioAcaoExtensao.mbean.readOnly}" onclick="this.form.target='_self';"/> 
						<h:commandButton value="#{relatorioAcaoExtensao.mbean.confirmButton}" action="#{relatorioAcaoExtensao.mbean.removerRelatorio}" onclick="this.form.target='_self';return confirm('Deseja Remover este Relatório?')" rendered="#{relatorioAcaoExtensao.mbean.readOnly}" />
						<h:commandButton value="Cancelar" action="#{relatorioAcaoExtensao.mbean.cancelar}" onclick="this.form.target='_self';#{confirm }" immediate="true"/>
				</td>
			</tr>
		</tfoot>
		
	</table>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>