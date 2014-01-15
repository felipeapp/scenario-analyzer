<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
<h:messages showDetail="true"></h:messages>
<h2><ufrn:subSistema /> > Dados Adicionais do Produto</h2>

<div class="descricaoOperacao">
	<table width="100%">
		<tr>
			<td width="50%">
			Nesta tela devem ser informados os dados adicionais de uma Ação.
			</td>
			<td>
				<%@include file="passos_atividade.jsp"%>
			</td>
		</tr>
	</table>
</div>

<h:form id="produto"> 
<table class=formulario width="95%">
<caption class="listagem">Dados Adicionais do Produto</caption>

	<h:inputHidden value="#{atividadeExtensao.obj.id}"/>

	<tr>
	<th  class="required" width="15%"> Tipo de produto:</th>
	<td>
		<h:selectOneMenu  id="tipoProduto" value="#{atividadeExtensao.obj.produtoExtensao.tipoProduto.id}"
			readonly="#{produtoExtensao.readOnly}">	
			<f:selectItem itemValue="0" itemLabel="-- SELECIONE O TIPO DE PRODUTO --"/>
			<f:selectItems value="#{tipoProduto.allAtivosCombo}"/>
		</h:selectOneMenu>
	</td>
	</tr>

	<tr>
		<th class="required"> Tiragem:</th>
		<td>
			<h:inputText  id="tiragem" value="#{atividadeExtensao.obj.produtoExtensao.tiragem}"	maxlength="8" readonly="#{atividadeExtensao.readOnly}" size="8" onkeyup="formatarInteiro(this)" />
			exemplares
		</td>
	</tr>


	<tr>
		<td colspan="2">
			<div id="tabs-dados-projeto" class="reduzido">
				<div id="resumo" class="aba">
					<i class="required">Resumo do Produto:</i><br/>
					<h:inputTextarea id="resumo" value="#{atividadeExtensao.obj.projeto.resumo}" rows="10" style="width:99%" />
				</div>
				
				<div id="justificativa" class="aba">
					<i class="required">Justificativa para execução do produto:</i><br/>
					<h:inputTextarea id="justificativa" value="#{atividadeExtensao.obj.justificativa}" rows="10" style="width:99%" />
				</div>

				<div id="objetivosGerais" class="aba">
					<i class="required">Objetivos Gerais.</i><br/>
					<h:inputTextarea id="objetivosGerais" value="#{atividadeExtensao.obj.projeto.objetivos}" rows="10" style="width:99%" />
				</div>

				<div id="resultados" class="aba">
					<i class="required">Resultados Esperados.</i><br/>
					<h:inputTextarea id="resultados" value="#{atividadeExtensao.obj.projeto.resultados}" rows="10" style="width:99%" />
				</div>
				
			</div>
		</td>
	</tr>

	<tfoot>
		<tr>
			<td colspan=2>
				<h:commandButton value="<< Voltar" action="#{atividadeExtensao.passoAnterior}" />
				<h:commandButton value="Cancelar" action="#{atividadeExtensao.cancelar}"  onclick="#{confirm}" />
				<h:commandButton value="Avançar >>" action="#{produtoExtensao.submeterProduto }" />
			</td>
		</tr>
	</tfoot>

</table>
</h:form>
</f:view>

<script type="text/javascript">
	var Tabs = {
	    init : function(){
	        var tabs = new YAHOO.ext.TabPanel('tabs-dados-projeto');
	        tabs.addTab('resumo', "Resumo");
	        tabs.addTab('justificativa', "Justificativa");
	        tabs.addTab('objetivosGerais', "Objetivos Gerais");
	        tabs.addTab('resultados', "Resultados Esperados");
	        tabs.activate('resumo');
	    }
	}
	YAHOO.ext.EventManager.onDocumentReady(Tabs.init, Tabs, true);
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>