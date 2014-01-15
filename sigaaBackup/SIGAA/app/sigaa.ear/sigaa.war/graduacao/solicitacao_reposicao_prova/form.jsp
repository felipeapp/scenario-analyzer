<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="solicitacaoReposicaoProva"/>	
	<h2> <ufrn:subSistema /> &gt; Reposi��o de Avalia��o &gt; Solicitar de Reposi��o de Avalia��o &gt; Selecionar Turma </h2>
	<div class="descricaoOperacao">
		<p>
			<b> Caro Aluno, </b>
		</p> 	
		<p>Atrav�s deste formul�rio ser� poss�vel solicitar reposi��o de avalia��o.</p>
		<br/>
		<p>Ser� necess�rio informar a justificativa pela qual foi perdida a avalia��o, e ainda � poss�vel enviar um documento que comprove a aus�ncia.</p>
		<br/>
		<p><b>Esta solicita��o n�o garante a Reposi��o da Avalia��o.</b></p>
		<p>Ap�s cadastramento desta solicita��o, o docente receber� um email informando da solicita��o da reposi��o da avalia��o, que ser� analisada, DEFERIDA ou INDEFERIDA.</p>
		<br/>
		<p><b>Art. 101.</b> Impedido de participar de qualquer avalia��o, por motivo de caso fortuito ou for�a
		maior devidamente comprovado e justificado, o aluno tem direito de realizar avalia��o de
		reposi��o.</p>
	</div>
<h:form id="form" enctype="multipart/form-data">
	<table class="formulario" style="width: 90%">
		<caption> Reposi��o de Avali��o </caption>
		<tr>
			<td colspan="2" class="subFormulario">Dados da Avalia��o Perdida</td>
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
						<th>Avalia��o:</th>
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
			<td colspan="2" class="subFormulario">Dados da Reposi��o</td>
		</tr>					
		<tr>
			<th class="obrigatorio">Justificativa:</th>
			<td>
				<h:inputTextarea cols="100" rows="5" id="justificativa" value="#{ solicitacaoReposicaoProva.obj.justificativa }"/><ufrn:help>Informe o Motivo pelo qual est� solicitando a Reposi��o de Avalia��o.</ufrn:help>
			</td>
		</tr>
		<tr>
			<th>Anexar Arquivo:</th>
			<td>
				<t:inputFileUpload value="#{ solicitacaoReposicaoProva.arquivo }" id="anexo" size="100"/>
				<ufrn:help>Selecione o arquivo que comprove a aus�ncia na avalia��o para ser enviado como anexo. (Tamanho m�ximo: ${solicitacaoReposicaoProva.tamanhoMaxArquivo} MB)</ufrn:help>			
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


