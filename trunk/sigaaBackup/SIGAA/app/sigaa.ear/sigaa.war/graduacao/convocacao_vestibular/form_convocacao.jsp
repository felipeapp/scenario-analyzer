
<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:keepAlive beanName="convocacaoVestibular"></a4j:keepAlive>
<h2><ufrn:subSistema /> &gt; Convoca��o de Candidatos para Vagas Remanescentes </h2>
<div class="descricaoOperacao">
	<p>Caro Usu�rio,</p>
	<p>Esta opera��o permite convocar candidatos suplentes de um processo seletivo vestibular para assumir vagas remanescentes.</p>
	<p>Caso deseje convocar candidatos para cadastro reserva, informe um percentual de vagas adicionais a convocar.</p> 
</div>

<h:form id="form">

<table class="formulario" width="80%">
	<caption>Dados da Convoca��o</caption>
	<tr>
		<th class="obrigatorio">Processo Seletivo:</th>
		<td>
			<h:selectOneMenu id="selectPsVestibular" value="#{convocacaoVagasRemanescentesVestibularMBean.obj.processoSeletivo.id}" onchange="submit()"
				valueChangeListener="#{ convocacaoVagasRemanescentesVestibularMBean.processoSeletivoListener }">
				<f:selectItems id="itensPsVestibular" value="#{convocacaoVagasRemanescentesVestibularMBean.processoSeletivoVestibularCombo}" />
			</h:selectOneMenu>
		</td>
	</tr>
	<tr>
		<th class="rotulo"> Estrat�gia de convoca��o:
		</th>
		<td>
			<h:outputText value="#{convocacaoVagasRemanescentesVestibularMBean.obj.processoSeletivo.estrategiaConvocacao.class.simpleName}"/>
			<h:outputText value="N�o definido" rendered="#{ empty convocacaoVagasRemanescentesVestibularMBean.obj.processoSeletivo.estrategiaConvocacao.class.simpleName}"/>
		</td>
	</tr>
	<tr>
		<th class="obrigatorio">Descri��o:</th>
		<td>
			<h:inputText id="descricao" value="#{convocacaoVagasRemanescentesVestibularMBean.obj.descricao}" size="80" maxlength="100"/>
			<ufrn:help>Ex.: 1� chamada para preenchimento das vagas remanescentes do Vestibular 2011</ufrn:help> 
		</td>
	</tr>
	<c:if test="${ convocacaoVagasRemanescentesVestibularMBean.obj.processoSeletivo.entradaDoisPeriodos }">
		<tr>
			<th class="required">Semestre a Convocar:</th>
			<td>
				<h:selectOneRadio id="semestreConvocacao" value="#{ convocacaoVagasRemanescentesVestibularMBean.obj.semestreConvocacao }"
					layout="pageDirection">
					<f:selectItems value="#{ convocacaoVestibular.semestresConvocacaoCombo }" />
				</h:selectOneRadio>
			</td>
		</tr>
	</c:if>
	<tr>
		<th class="obrigatorio">Data da Convoca��o:</th>
		<td>
			<t:inputCalendar id="dataConvocacao"
				value="#{convocacaoVagasRemanescentesVestibularMBean.obj.dataConvocacao}"
				size="10" maxlength="10" 
			    onkeypress="return(formatarMascara(this,event,'##/##/####'))" 
			    popupDateFormat="dd/MM/yyyy" 
			    renderAsPopup="true" renderPopupButtonAsImage="true" >
			      <f:converter converterId="convertData"/>
			</t:inputCalendar> 
		</td>
	</tr>
	<tr>
		<th class="obrigatorio">Percentual Adicional de Vagas:</th>
		<td>
			<h:inputText id="percentualAdicional" value="#{convocacaoVagasRemanescentesVestibularMBean.obj.percentualAdicionalVagas}" 
				 converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" size="3" maxlength="3" />% 
			<ufrn:help>O n�mero de vagas ser� aumentado no percentual indicado, convocando discentes al�m das vagas ofertadas.</ufrn:help> 
		</td>
	</tr>
	<tr>
		<th><h:selectBooleanCheckbox value="#{convocacaoVagasRemanescentesVestibularMBean.aplicaPercentualCotistas}" id="aplicaPercentualCotistas"/> </th>
		<td>
			<label for="incluirPendenteCadastro" onclick="$('form:aplicaPercentualCotistas').checked = !$('form:aplicaPercentualCotistas').checked;">
				Aplicar o Percentual Adicional de Vagas � vagas reservadas para Cotistas.
			</label>
		</td>
	</tr>
	<tr>
		<th><h:selectBooleanCheckbox value="#{convocacaoVagasRemanescentesVestibularMBean.incluirPendenteCadastro}" id="incluirPendenteCadastro"/> </th>
		<td>
			<label for="incluirPendenteCadastro" onclick="$('form:incluirPendenteCadastro').checked = !$('form:incluirPendenteCadastro').checked;">
				Considerar discentes com status PENDENTE DE CADASTRO como ocupando vaga remanescente.
			</label>
		</td>
	</tr>
	<tr>
		<th><h:selectBooleanCheckbox value="#{convocacaoVagasRemanescentesVestibularMBean.incluirPreCadastrado}" id="incluirPreCadastrado"/> </th>
		<td>
			<label for="incluirPendenteCadastro" onclick="$('form:incluirPreCadastrado').checked = !$('form:incluirPreCadastrado').checked;">
				Considerar discentes com status PR�-CADASTRADO como ocupando vaga remanescente.
			</label>
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="Cancelar" action="#{ convocacaoVagasRemanescentesVestibularMBean.cancelar }" id="btnCancelar" onclick="#{confirm}" immediate="true"/>
				<h:commandButton id="btnBuscar" value="Pr�ximo Passo >>" action="#{convocacaoVagasRemanescentesVestibularMBean.buscarVagasRemanescentes}"/>
			</td>
		</tr>
	</tfoot>
</table>
<br/>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</h:form>


</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>