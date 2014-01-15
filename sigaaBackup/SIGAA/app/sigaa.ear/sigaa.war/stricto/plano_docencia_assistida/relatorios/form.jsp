<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="relatoriosDocenciaAssistidaMBean" />
	<h2> <ufrn:subSistema /> &gt; ${relatoriosDocenciaAssistidaMBean.tituloRelatorio}</h2>
<h:form id="form">

	<table class="formulario" style="width: 80%">
	<caption> Informe os critérios de Busca</caption>
		<tr>
			<th width="155px">
				<label for="checkUnidade">Programa:	</label>				
			</th>				
			<td>
				<h:selectOneMenu id="programa" style="width : 550px;" value="#{relatoriosDocenciaAssistidaMBean.unidade.id}">
					<f:selectItem itemLabel="--SELECIONE--" itemValue="0"/>
					<f:selectItems value="#{unidade.allProgramaPosCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>					
			<th>
				<label for="checkNivel">Nível:</label>				
			</th>				
			<td>
			   	<h:selectOneMenu value="#{relatoriosDocenciaAssistidaMBean.nivel}" style="width : 250px;" id="nivelCombo">
					<f:selectItem itemLabel="--SELECIONE--" itemValue="0"/>
					<f:selectItems value="#{nivelEnsino.strictoCombo}"/>
				</h:selectOneMenu>				
			</td>
		</tr>
		<tr>
			<th>
				<label for="checkTipoBolsa">Modalidade da Bolsa:</label>				
			</th>				
			<td>
			   	<h:selectOneMenu value="#{relatoriosDocenciaAssistidaMBean.modalidadeBolsa.id}" style="width : 250px;" onchange="J('#checkTipoBolsa').attr('checked', 'true');" id="modalidadeCombo">
					<f:selectItem itemLabel="--SELECIONE--" itemValue="0"/>
					<f:selectItems value="#{planoDocenciaAssistidaMBean.allModalidadeBolsaCombo}"/>
				</h:selectOneMenu>				
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">
				<label for="checkAnoPeriodo">Ano-Período:</label>
			</th>
			<td>
				<h:inputText value="#{relatoriosDocenciaAssistidaMBean.ano}" size="4" maxlength="4" 
						id="inputAno" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" /> .
				<h:inputText value="#{relatoriosDocenciaAssistidaMBean.periodo}" size="1" maxlength="1" 
						id="inputPeriodo" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" />
			</td>
		</tr>
		<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton id="btBuscar" action="#{relatoriosDocenciaAssistidaMBean.gerarRelatorio}" value="Gerar Relatório"/>
				<h:commandButton id="btCancelar" action="#{relatoriosDocenciaAssistidaMBean.cancelar}" value="Cancelar" onclick="#{confirm}"/>
			</td>
		</tr>
		</tfoot>
	</table>	
</h:form>	

	<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	