<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h:form>
	
			<div class="descricaoOperacao" id="aviso">			
				<table width="100%">
					<tr>
					<td width="10%" align="center"><html:img page="/img/warning.gif" width="30px" height="30px"/> </td>
					<td align="justify">				 
						Prezado Docente,
						<br/>
						<br/>
						
						Seu projeto acad�mico foi submetido com sucesso. Caso seja um projeto com recursos externos e que necessite de um conv�nio, contrato ou termo de coopera��o, � necess�rio preencher dados adicionais para submiss�o � Pr�-Reitoria de Planejamento.
						<br/>
						<br/>
						
						<b>Clique <h:commandLink value="aqui" action="#{acessoRequisicoesProjetos.iniciarSubmeterPropostas}"/> para Submeter o projeto a PROPLAN.</b>
						<br/>
						<br/>

						D�vidas, ligar para 3215-3142.<br/>
					</td>
					</tr>
				</table>
			</div>
			<br/>
	
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>