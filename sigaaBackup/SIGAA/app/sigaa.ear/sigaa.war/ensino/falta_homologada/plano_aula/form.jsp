<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Criar Plano de Aula </h2>
<f:view>

<a4j:keepAlive beanName="faltaHomologada"></a4j:keepAlive>
<a4j:keepAlive beanName="planoReposicaoAula"></a4j:keepAlive>

	<div class="descricaoOperacao">
		<p>
			Caro Professor,
		</p>	
		<br/>
		<p>
			Nesta tela você irá preparar o plano de aula. Quando o plano de aula for aprovado, a turma receberá por email o conteúdo do que foi elaborado aqui. 
		</p>
	</div>

	<h:form id="form">
	
		<table class="formulario" width="95%">
			<caption>Elaboração do Plano de Aula</caption>
			<tr>
				<th>Disciplina:</th>
				<td>
					<h:outputText value="#{planoReposicaoAula.obj.faltaHomologada.dadosAusencia.docenteTurma.turma.disciplina.codigoNome}" />
				</td>
			</tr>
			<tr>
				<th>Turma:</th>
				<td>
					<h:outputText value="#{planoReposicaoAula.obj.faltaHomologada.dadosAusencia.docenteTurma.turma.codigo}" />
				</td>
			</tr>
			
			<tr>
				<th width="15%" nowrap="nowrap">Data da Falta:</th>
				<td>
					<h:outputText value="#{planoReposicaoAula.obj.faltaHomologada.dadosAusencia.dataAula}" />
				</td>
			</tr>			
			<tr>
				<td colspan="2" class="subFormulario">Plano de Aula</td>
			</tr>
			<tr>
				<th class="required">Data da Aula:</th>
				<td nowrap="nowrap">
					<t:inputCalendar id="dataFalta" value="#{planoReposicaoAula.obj.dataAulaReposicao}" size="10" maxlength="10" 
						renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return formataData(this, event)" popupDateFormat="dd/MM/yyyy">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th class="required">Conteúdo:</th>
				<td>
					<h:inputTextarea style="width: 90%" rows="8" value="#{planoReposicaoAula.obj.didatica}" />
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{planoReposicaoAula.confirmButton}" action="#{planoReposicaoAula.criarPlanoAula}" />
						<h:commandButton value="<< Voltar" action="#{planoReposicaoAula.btnVoltar}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	
	<br />
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" />
		 <span class="fontePequena">Campos de preenchimento obrigatório. </span> 
		<br>
		<br>
	</center>		
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>