<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Resumo do Plano de Aula </h2>
<f:view>

<a4j:keepAlive beanName="faltaHomologada"></a4j:keepAlive>
<a4j:keepAlive beanName="planoReposicaoAula"></a4j:keepAlive>

	<div class="descricaoOperacao">
		<p>
			Caro Professor,
			
			Aqui é exibido todas as informações do plano de aula elaborado para reposição de uma falta.
		</p>	
	</div>

	<h:form id="form">
	
		<table class="formulario" width="95%">
			<caption>Plano de Aula</caption>
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
				<th>Data da Aula:</th>
				<td nowrap="nowrap">
					<h:outputText value="#{planoReposicaoAula.obj.dataAulaReposicao}" />
				</td>
			</tr>
			<tr>
				<th>Conteúdo:</th>
				<td>
					<h:outputText value="#{planoReposicaoAula.obj.didatica}" />
				</td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario">Parecer Emitido</td>
			</tr>
			<c:if test="${not empty planoReposicaoAula.obj.parecer}">		
			<tr>
				<th>Status:</th>
				<td>
					<h:outputText value="#{planoReposicaoAula.obj.parecer.status.descricao}" />
				</td>
			</tr>
			<tr>
				<th>Justificativa:</th>
				<td>
					<h:outputText value="#{planoReposicaoAula.obj.parecer.justificativa}" />
				</td>
			</tr>			
			</c:if>
			<c:if test="${empty planoReposicaoAula.obj.parecer}">		
			<tr>
				<td colspan="4">
					<p style="text-align: center;padding: 5px;font-style: italic">
						O chefe de departamento ainda não emitiu um parecer sobre este plano de aula.
					</p>
				</td>
			</tr>
			</c:if>			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="<< Voltar" action="#{planoReposicaoAula.listarTodosPlanos}" />
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