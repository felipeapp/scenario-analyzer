<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<h2><ufrn:subSistema /> &gt; Manifestar Interesse em Avaliar Trabalhos</h2>

	<div class="descricaoOperacao">
		<p>
			Caso voc� tenha interesse em ser um avaliador de trabalho, realize seu cadastro que posteriormente ser� analisado. 
		</p>
	</div>
	
	<h:form>
	
		<table class="formulario" width="70%">
			<caption>Dados do Congresso</caption>
			<tr>
				<th class="rotulo">Descri��o do Congresso:</th>
				<td><h:outputText value="#{ interesseAvaliarBean.congresso.descricao }"/> </td>
			</tr>	
						
			<tr>
				<th class="rotulo">Per�odo:</th>
				<td> <h:outputText value="#{ interesseAvaliarBean.congresso.descricaoPeriodo }"/> </td>
			</tr>			
						
			
			<tr>
				<td colspan="2">
				
					<table class="subFormulario" width="100%">
						<caption>Cadastrar Avaliador do CIC</caption>
						<tr>
							<th class="obrigatorio">Tipo Avalidor:</th>
							<td>
								<h:selectBooleanCheckbox value="#{ interesseAvaliarBean.avaliador.avaliadorResumo }"/>Avaliador de Resumo
							</td>
							
							<td>
								<h:selectBooleanCheckbox value="#{ interesseAvaliarBean.avaliador.avaliadorApresentacao }"/>Avaliador de Apresenta��o
							</td>
						</tr>
					</table>
				</td>
			</tr>
			
			
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton id="btnCadastrar" value="Cadastrar" action="#{interesseAvaliarBean.cadastrar}"/>
					<h:commandButton id="btnCancelar" value="Cancelar" action="#{interesseAvaliarBean.cancelar}" onclick="#{confirm}" immediate="true"/>
				</td>
			</tr>
		</tfoot>
			
			
				
				
		</table>	
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>