<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="solicitacaoReposicaoProva"/>	
	<h2> <ufrn:subSistema /> &gt; Reposição de Avaliação &gt; Solicitar de Reposição de Avaliação &gt; Selecionar Turma </h2>
	<div class="descricaoOperacao">
		<p>
			<b> Caro Aluno, </b>
		</p> 	
		<p>Através deste formulário será possível solicitar reposição de avaliação.</p>
		<br/>
		<p>Será necessário informar a justificativa pela qual foi perdida a avaliação, e ainda é possível enviar um documento que comprove a ausência.</p>
		<br/>
		<p><b>Esta solicitação não garante a Reposição da Avaliação.</b></p>
		<p>Após cadastramento desta solicitação, o docente receberá um email informando da solicitação da reposição da avaliação, que será analisada, DEFERIDA ou INDEFERIDA.</p>
		<br/>
		<p><b>Art. 101.</b> Impedido de participar de qualquer avaliação, por motivo de caso fortuito ou força
		maior devidamente comprovado e justificado, o aluno tem direito de realizar avaliação de
		reposição.</p>
	</div>
<h:form id="form" enctype="multipart/form-data">
	<table class="formulario" style="width: 90%">
		<caption> Reposição de Avalição </caption>
		<tr>
			<td colspan="2" class="subFormulario">Dados da Avaliação Perdida</td>
		</tr>		
		<tr>
			<td colspan="2">
				<table class="visualizacao" style="width: 100%">
					<tr>
						<th>Turma:</th>
						<td>${solicitacaoReposicaoProva.turma.descricaoCodigo}</td>
					</tr>		
					<tr>
						<th>Professor(es):</th>
						<td>${solicitacaoReposicaoProva.turma.docentesNomes}</td>
					</tr>		
					<tr>
						<th>Avaliação:</th>
						<td>
							${solicitacaoReposicaoProva.obj.dataAvaliacao.descricao}
						</td>
					</tr>			
					<tr>
						<th>Data/Hora:</th>
						<td>
						    <h:outputText id="data" value="#{solicitacaoReposicaoProva.obj.dataAvaliacao.data}"/> - 			    
						    <h:outputText id="hora" value="#{solicitacaoReposicaoProva.obj.dataAvaliacao.hora}"/>									
						</td>
					</tr>									
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario">Dados da Reposição</td>
		</tr>					
		<tr>
			<th class="obrigatorio">Justificativa:</th>
			<td>
				<h:inputTextarea cols="100" rows="5" id="justificativa" value="#{ solicitacaoReposicaoProva.obj.justificativa }"/><ufrn:help>Informe o Motivo pelo qual está solicitando a Reposição de Avaliação.</ufrn:help>
			</td>
		</tr>
		<tr>
			<th>Anexar Arquivo:</th>
			<td>
				<t:inputFileUpload value="#{ solicitacaoReposicaoProva.arquivo }" id="anexo" size="100"/>
				<ufrn:help>Selecione o arquivo que comprove a ausência na avaliação para ser enviado como anexo. (Tamanho máximo: ${solicitacaoReposicaoProva.tamanhoMaxArquivo} MB)</ufrn:help>			
			</td>
		</tr>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Confirmar" action="#{solicitacaoReposicaoProva.beforeCadastrar}"/>
					<h:commandButton value="<< Selecionar Outra Turma" action="#{solicitacaoReposicaoProva.iniciar}"/>
					<h:commandButton value="Cancelar" action="#{solicitacaoReposicaoProva.cancelar}" onclick="#{confirm}" immediate="true"/>
				</td>
			</tr>
		</tfoot>					
	</table>

	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>


