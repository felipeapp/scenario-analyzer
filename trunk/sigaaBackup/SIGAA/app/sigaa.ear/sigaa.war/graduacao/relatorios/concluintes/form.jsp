<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form id="form">
<h2> <ufrn:subSistema /> > ${relatoriosConcluintesMBean.tituloRelatorio} </h2>

<c:if test="${relatoriosConcluintesMBean.potenciaisConcluintes}">
	<div class="descricaoOperacao">
		<p>
			Potenciais Concluintes são os alunos que são formandos e que possuem a última matricula no Ano/Semestre indicado. 		
		</p>
		<p>
			Isso significa que se ele tiver a matrícula consolidada passará a ser Concluinte (Graduando).
		</p>
	</div>
</c:if>

<table class="formulario" style="width:70%">

<caption> Informe os critérios para a emissão do relatório </caption>

	<tr>
		<th> Unidade: </th>
		<td>
			<h:selectOneMenu id="unidade" value="#{relatoriosConcluintesMBean.unidade.id}">
				<f:selectItem itemValue="0" itemLabel="UFRN"/>
				<f:selectItems value="#{unidade.allCentrosEscolasCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>

	<tr>
		<th class="obrigatorio"> Ano: </th>
		<td>
			<h:inputText id="ano" value="#{relatoriosConcluintesMBean.ano}" size="4" maxlength="4" 
			onkeyup="formatarInteiro(this)" converter="#{ intConverter }"/>
			
			<h:outputText value="Semestre:" styleClass="obrigatorio" rendered="#{relatoriosConcluintesMBean.potenciaisConcluintes}"/>
			<h:inputText id="semestre" value="#{relatoriosConcluintesMBean.periodo}" size="2" maxlength="1" 
			rendered="#{relatoriosConcluintesMBean.potenciaisConcluintes}" 
			onkeyup="formatarInteiro(this)" converter="#{ intConverter }"/>
		</td>
	</tr>
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatoriosConcluintesMBean.gerarRelatorio}" value="Emitir Relatório"/>
			<h:commandButton action="#{relatoriosConcluintesMBean.cancelar}" value="Cancelar" onclick="#{confirm}"/>
		</td>
	</tr>
	</tfoot>
</table>

</h:form>
<div class="obrigatorio">Campo de preenchimento obrigatório.</div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>