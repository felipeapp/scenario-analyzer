<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Criar Plano de Aula </h2>
<f:view>

<a4j:keepAlive beanName="avisoFaltaHomologada"></a4j:keepAlive>
<a4j:keepAlive beanName="planoReposicaoAula"></a4j:keepAlive>

	<div class="descricaoOperacao">
		<p>
			Prezado Professor,
		</p>	
		<br/>
		<p>
			A chefia da sua unidade homologou a não realização da aula no dia <h:outputText value="#{planoReposicaoAula.obj.faltaHomologada.dadosAvisoFalta.dataAula}" />. A legislação da carreira docente exige o comparecimento em todas as aulas podendo ser aplicado a falta. No entanto, o sr(a). pode apresentar um plano de reposição para esta aula.
		</p>
		<p>
			Apresente abaixo um plano de reposição que retornará para a chefia da unidade. Após a homologação, o plano informado será notificado por e-mail para conhecimento dos alunos da turma e adicionado automaticamente como um tópico de aula na turma virtual, que terá como conteúdo o informado no campo 'Conteúdo/Metodologia de Reposição da Aula' abaixo.
		</p> 
	</div>

	<h:form id="form">
	
		<table class="formulario" width="95%">
			<caption>Elaboração do Plano de Aula</caption>
			<tr>
				<th style="font-weight: bold">Disciplina:</th>
				<td>
					<h:outputText value="#{planoReposicaoAula.obj.faltaHomologada.dadosAvisoFalta.turma.disciplina.codigoNome}" />
				</td>
			</tr>
			<tr>
				<th style="font-weight: bold">Turma:</th>
				<td>
					<h:outputText value="#{planoReposicaoAula.obj.faltaHomologada.dadosAvisoFalta.turma.codigo}" />
				</td>
			</tr>
			
			<tr>
				<th width="15%" nowrap="nowrap" style="font-weight: bold">Data da Aula:</th>
				<td>
					<h:outputText value="#{planoReposicaoAula.obj.faltaHomologada.dadosAvisoFalta.dataAula}" />
				</td>
			</tr>			
			<tr>
				<td colspan="2" class="subFormulario">Plano de Aula</td>
			</tr>
			<tr>
				<th class="required">Data de Reposição da Aula:</th>
				<td nowrap="nowrap">
					<t:inputCalendar id="dataFalta" value="#{planoReposicaoAula.obj.dataAulaReposicao}" size="10" maxlength="10" 
						renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return formataData(this, event)" popupDateFormat="dd/MM/yyyy">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th class="required">Conteúdo/Metodologia de Reposição da Aula:</th>
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