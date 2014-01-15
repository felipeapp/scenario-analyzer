<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Parecer de Plano de Aula </h2>
<f:view>

	<a4j:keepAlive beanName="parecerPlanoReposicaoAula"></a4j:keepAlive>

	<div class="descricaoOperacao">
		<p>
			Nesta tela � apresentado o plano de aula elaborado pelo professor da disciplina.
		</p>	
	</div>

	<h:form>
	
		<table class="formulario" width="85%">
			<caption>Parecer do Plano de Aula</caption>
			
			<tr>
				<td class="subFormulario" colspan="2">Informa��o Sobre a Falta</td>
			</tr>
			<tr>
				<th width="5%" nowrap="nowrap">Nome:</th>
				<td>
					<h:outputText value="#{parecerPlanoReposicaoAula.obj.planoAula.faltaHomologada.dadosAusencia.docenteTurma.docente.nome}" />					
				</td>
			</tr>
			<tr>
				<th width="5%" nowrap="nowrap">Disciplina:</th>
				<td>
					<h:outputText value="#{parecerPlanoReposicaoAula.obj.planoAula.faltaHomologada.dadosAusencia.docenteTurma.turma.disciplina.codigoNome}" />					
				</td>
			</tr>
			<tr>
				<th width="5%" nowrap="nowrap">Turma:</th>
				<td>
					<h:outputText value="#{parecerPlanoReposicaoAula.obj.planoAula.faltaHomologada.dadosAusencia.docenteTurma.turma.codigo}" />					
				</td>
			</tr>
			<tr>
				<th width="5%" nowrap="nowrap">Data da Falta:</th>
				<td>
					<h:outputText value="#{parecerPlanoReposicaoAula.obj.planoAula.faltaHomologada.dadosAusencia.dataAula}" />					
				</td>
			</tr>			
			<tr>
				<td class="subFormulario" colspan="2">Plano de Aula</td>
			</tr>																																
			<tr>
				<th width="5%" nowrap="nowrap">Data Reposi��o:</th>
				<td>
					<h:outputText value="#{parecerPlanoReposicaoAula.obj.planoAula.dataAulaReposicao}" />					
				</td>
			</tr>			
			<tr>
				<th>Conte�do:</th>
				<td>
					<h:outputText value="#{parecerPlanoReposicaoAula.obj.planoAula.didatica}" />
				</td>
			</tr>
			<tr>
				<td class="subFormulario" colspan="2">Parecer Sobre o Plano</td>
			</tr>				
			<tr>
				<th class="required">Parecer:</th>
				<td>
					<h:selectOneMenu value="#{parecerPlanoReposicaoAula.obj.status.id}">
						<f:selectItems value="#{parecerPlanoReposicaoAula.possiveisStatusParecerCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>				
			<tr>
				<th class="required">Justificativa:</th>
				<td>
					<h:inputTextarea rows="5" style="width: 95%" value="#{parecerPlanoReposicaoAula.obj.justificativa}" />
				</td>
			</tr>					
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Emitir Parecer" action="#{parecerPlanoReposicaoAula.emitirParecer}" />
						<h:commandButton value="<< Voltar" action="#{planoReposicaoAula.listarPlanoAulaPendentesAprovacao}" immediate="true" />
						<h:commandButton value="Cancelar" action="#{parecerPlanoReposicaoAula.cancelar}" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br />
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" />
		 <span class="fontePequena">Campos de preenchimento obrigat�rio. </span> 
		<br>
		<br>
	</center>	
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>