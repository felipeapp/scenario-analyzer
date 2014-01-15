<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h2> <ufrn:subSistema /> > 
<h:outputText value="Estorno de Conclusão Coletiva" rendered="#{ !colacaoColetiva.apenasAlterarDataColacao }" />
<h:outputText value="Alterar Data de Colação Coletiva" rendered="#{ colacaoColetiva.apenasAlterarDataColacao }" /> 
</h2>
<h:form>
<a4j:keepAlive beanName="colacaoColetiva"></a4j:keepAlive>
	<table class="formulario" width="85%">
		<caption>Selecione o curso e Data de Conclusão</caption>
		<tr>
			<th width="15%" class="required">Curso:</th>
			<td>
				<h:selectOneMenu value="#{colacaoColetiva.curso.id}" id="curso">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{curso.allCursoGraduacaoCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th class="required">Ano-Período:</th>
			<td>
				<h:inputText value="#{colacaoColetiva.ano}" id="ano" size="4" maxlength="4" onkeyup="return formatarInteiro(this);"/> -
				<h:inputText value="#{colacaoColetiva.periodo}" id="periodo" size="1" maxlength="1" onkeyup="return formatarInteiro(this);" />
			</td>
		</tr>
		<tfoot>
			<tr>
			<td colspan="2" align="center">
			<h:commandButton action="#{colacaoColetiva.buscarConcluintes}" value="Buscar Concluintes deste Curso" id="concluintesDoCurso"/>
			<h:commandButton action="#{colacaoColetiva.cancelar}" value="Cancelar" onclick="#{confirm}" id="CancelarColacaoColetiva"/>
			</td>
			</tr>
		</tfoot>
	</table>
	
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>