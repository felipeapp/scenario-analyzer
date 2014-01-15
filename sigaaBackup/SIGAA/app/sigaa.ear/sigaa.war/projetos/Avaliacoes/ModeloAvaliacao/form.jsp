<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> &gt; Modelo de Avaliação</h2>
	<a4j:keepAlive beanName="modeloAvaliacao"></a4j:keepAlive>
	<br />

	<table class=formulario width="80%">
		<h:form prependId="false">
			<caption class="listagem">Cadastro de Modelos de Avaliações</caption>
			<h:inputHidden value="#{modeloAvaliacao.confirmButton}" />
			<h:inputHidden value="#{modeloAvaliacao.obj.id}" />
			<tr>
				<th width="20%" class="obrigatorio">Título:</th>
				<td><h:inputText size="80" maxlength="200"
					readonly="#{modeloAvaliacao.readOnly}"  value="#{modeloAvaliacao.obj.descricao}" /></td>
			</tr>
			<tr>
				<th class="obrigatorio">Média para Aprovação:</th>
				<td>
					<h:inputText value="#{modeloAvaliacao.obj.mediaMinimaAprovacao}" label="Média para Aprovação" 
						style="text-align: right;" id="mediaMinimaAprovacao" size="4" maxlength="4"
							onblur="comma2Point(this); verificaNotaMaiorDez(this);" onkeydown="return(formataValor(this, event, 2));" />
					<ufrn:help>Média mínima para aprovação dos projetos avaliados que usam este Modelo.</ufrn:help>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Máxima Discrepância:</th>
				<td>
					<h:inputText value="#{modeloAvaliacao.obj.maximaDiscrepanciaAvaliacoes}" label="Máxima Discrepância" 
						style="text-align: right;" id="maximaDiscrepanciaAvaliacoes" size="4" maxlength="4"
							onblur="comma2Point(this); verificaNotaMaiorDez(this);" onkeydown="return(formataValor(this, event, 2));" />
					<ufrn:help>Máxima discrepância permitida entre a maior e a menor nota das avaliações.</ufrn:help>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Tipo:</th>
				<td>
					<h:selectOneMenu id="comboTipo" value="#{modeloAvaliacao.obj.tipoAvaliacao.id}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
                		<f:selectItems value="#{modeloAvaliacao.allComboTipo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>Edital:</th>
				<td>
					<h:selectOneMenu id="comboEdital" value="#{modeloAvaliacao.obj.edital.id}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
                		<f:selectItems value="#{editalMBean.allCombo}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Questionário:</th>
				<td>
					<h:selectOneMenu id="comboQuest" value="#{modeloAvaliacao.obj.questionario.id}" 
						valueChangeListener="#{modeloAvaliacao.carregaQuestionario}"
						onchange="submit();showQues(this);" >
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
                		<f:selectItems value="#{modeloAvaliacao.allComboQuestionario}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<c:if test="${modeloAvaliacao.confirmButton == 'Alterar' }" >
						<td colspan="2"><h:commandButton value="Alterar" action="#{modeloAvaliacao.cadastrar}" />
					</c:if>
					<c:if test="${modeloAvaliacao.confirmButton == 'Cadastrar'}">
						<td colspan="2"><h:commandButton value="Cadastrar" action="#{modeloAvaliacao.cadastrar}" />
					</c:if>
					 <h:commandButton value="Cancelar" onclick="#{confirm}" action="#{modeloAvaliacao.cancelar}" />
					 </td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br>
	<table id="showQuestionario" class="formulario" width="100%" style="display: ${modeloAvaliacao.obj.questionario.id == 0 ? 'none' : ''}">
		<caption>Questionário de Avaliação ${modeloAvaliacao.obj.questionario.descricao}</caption>
		<tr>
			<td>
				<h:dataTable id="dtView" value="#{modeloAvaliacao.obj.questionario.itensAvaliacao}" 
					rowClasses="linhaPar, linhaImpar" var="itemQues" width="100%">
						<h:column>
							<f:facet name="header">
								<h:outputText value="Pergunta" />
							</f:facet>
							<h:outputText value="#{itemQues.pergunta.descricao}" />
						</h:column>
						<h:column>
							<f:facet name="header">
								<h:outputText value="Grupo" />
							</f:facet>
							<h:outputText value="#{itemQues.grupo.descricao}" />
						</h:column>
						<h:column>
						
							<f:facet name="header">
								<h:outputText value="Peso" />
							</f:facet>
							<h:outputText value="#{itemQues.peso}" />
						</h:column>
						<h:column>
							<f:facet name="header">
								<h:outputText value="Nota Máxima" />
							</f:facet>
							<h:outputText value="#{itemQues.notaMaxima}" />
						</h:column>
				</h:dataTable>
			</td>	
		</tr>
	</table>
	<br />
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	
</f:view>

<script type="text/javascript" src="${ctx}/javascript/projetos/functions.js"></script>
<script type="text/javascript">

	function showQues(obj){
		if(obj.value != 0){
			$('showQuestionario').style.display = "";
		}else{
			$('showQuestionario').style.display = "none";
		}
	}
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
