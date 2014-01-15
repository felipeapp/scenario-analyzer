<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />

	<h2><ufrn:subSistema /> &gt; Usuário Externo da Biblioteca &gt; Cancelar Vínculo</h2>

	<h:form>
	
	<div class="descricaoOperacao">
		<p>Utilize este formulário para cancelar o vínculo do usuário externo para que ele não possa mais utilizar os serviços da biblioteca.</p>
	</div>
	

		<c:if test="${usuarioExternoBibliotecaMBean.obj.cancelado}">
			
			<table class="formulario" width="70%">
				<caption>Informe o motivo do cancelamento do vínculo do usuário externo</caption>
				
				<tr>
					<th style="width: 20%; font-weight: bold;">CPF:</th>
					<td>${usuarioExternoBibliotecaMBean.pessoa.cpf_cnpj}</td>
				</tr>
				<tr>
					<th style="font-weight: bold;"  >Nome:</th>
					<td>${usuarioExternoBibliotecaMBean.pessoa.nome}</td>
				</tr>
				
				<tr>
					<th style="font-weight: bold;">Final do Prazo:</th>
					<td style="${usuarioExternoBibliotecaMBean.obj.canceladoPorPrazo ? "color:red;" : " " }"> <ufrn:format type="data" valor="${usuarioExternoBibliotecaMBean.obj.prazoVinculo}"/></td>
				</tr>
				
				<tr>
					<th style="font-weight: bold;" >Motivo:</th>
					<td style="${usuarioExternoBibliotecaMBean.obj.canceladoManualmenente ? "color:red;" : " " }">
						${usuarioExternoBibliotecaMBean.obj.motivoCancelamento}
					</td>
				</tr>
				
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton action="#{buscaUsuarioBiblioteca.telaBuscaUsuarioBiblioteca}" value="<< Voltar" />
							<h:commandButton action="#{usuarioExternoBibliotecaMBean.cancelar}" value="Cancelar" immediate="true" onclick="#{confirm}" />
						</td>
					</tr>
				</tfoot>
			</table>
			
		</c:if>
		
		<c:if test="${! usuarioExternoBibliotecaMBean.obj.cancelado}">
		
			<table class="formulario" width="70%">
				<caption>Informe o motivo do cancelamento do vínculo do usuário externo</caption>
					<tr>
						<th style="font-weight: bold;">CPF:</th>
						<td>${usuarioExternoBibliotecaMBean.pessoa.cpf_cnpj}</td>
					</tr>
					<tr>
						<th style="font-weight: bold;">Nome:</th>
						<td>${usuarioExternoBibliotecaMBean.pessoa.nome}</td>
					</tr>
					
					<tr>
						<th style="font-weight: bold;">Final do Prazo:</th>
						<td> <ufrn:format type="data" valor="${usuarioExternoBibliotecaMBean.obj.prazoVinculo}"/></td>
					</tr>
					
					<tr>
						<th class="obrigatorio">Motivo:</th>
						<td>
							<h:inputTextarea value="#{usuarioExternoBibliotecaMBean.obj.motivoCancelamento}" cols="80" rows="4"/>
						</td>
					</tr>
	
					<tr>
						<td colspan="2">
							<c:set var="exibirApenasSenha" value="true" scope="request"/>
							<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
						</td>
					</tr>
	
					<tfoot>
						<tr>
							<td colspan="2">
								<h:commandButton action="#{usuarioExternoBibliotecaMBean.cancelarVinculo}" value="#{usuarioExternoBibliotecaMBean.confirmButton}" />
								<h:commandButton action="#{buscaUsuarioBibliotecaMBean.telaBuscaUsuarioBiblioteca}" value="Cancelar" immediate="true"  onclick="#{confirm}"/>
							</td>
					</tfoot>
			</table>
			
			
			
		</c:if>
			
		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
			
		
		
	</h:form>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>