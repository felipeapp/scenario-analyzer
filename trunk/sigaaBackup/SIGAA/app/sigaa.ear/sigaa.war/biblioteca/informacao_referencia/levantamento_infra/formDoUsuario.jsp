<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<f:view>
	
	<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; Solicitar Levantamento de Infra-Estrutura</h2>
	
	<h:messages showDetail="true" />
	
	<div class="descricaoOperacao">
		<p>Caro usu�rio,</p> 
		<p>Escolha a Biblioteca para onde a solicita��o ser� enviada e informe os detalhes dela. </p>
		<p>Normalmente as solicita��es s�o encaminhadas � Biblioteca do seu Centro, caso essa Biblioteca n�o realize Levantamento de 
		Infra-Estrutura, ela ser� encaminhada � Biblioteca Central.</p>
		<p>O senhor ser� avisado por email quando a solicita��o for atendida.</p>
		<p>Aten��o: S� ser� poss�vel cancelar uma solicita��o se um bibliotec�rio ainda n�o tiver come�ado a atend�-la.</p>
	</div>
	
	<h:form id="form">
	
		<a4j:keepAlive beanName="levantamentoInfraMBean" />
	
		<table class="formulario" style="width: 70%;">
			
			<caption>Dados da Solicita��o</caption>
			
			<tbody>
			
				<tr>
					<th>Biblioteca:</th>
					<td>
						<h:selectOneMenu id="biblioteca" 
								value="#{ levantamentoInfraMBean.obj.biblioteca.id }" >
							<f:selectItems value="#{ levantamentoInfraMBean.bibliotecasDoUsuarioCombo }" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>Detalhes:</th>
					<td>
						<h:inputTextarea id="textoSolicitacao"
							value="#{ levantamentoInfraMBean.obj.textoSolicitacao }"
							cols="70" rows="10"/>
					</td>
				</tr>
			
			</tbody>
			
			<tfoot><tr><td colspan="2">
			
				<h:commandButton value="Solicitar" id="solicitarInfra" action="#{ levantamentoInfraMBean.solicitar }" onclick="if (!confirm('Confirma a cria��o da solicita��o ?')) return false" />
				<h:commandButton value="<< Voltar" id="voltarListaSolicitacoesInfra" action="#{ levantamentoInfraMBean.telaListaDoUsuario }" immediate="true"/>
				<h:commandButton value="Cancelar" id="cancelarSolicitacaoInfra" action="#{ levantamentoInfraMBean.cancelar }" onclick="#{confirm}" immediate="true"/>
				
			</td></tr></tfoot>
			
		</table>
		
	
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>