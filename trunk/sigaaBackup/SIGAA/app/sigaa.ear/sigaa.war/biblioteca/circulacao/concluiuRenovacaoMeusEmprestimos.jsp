<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h:form id="formConcluiuRenovacaoMeusEmprestimos">
	
	<h2><ufrn:subSistema /> &gt; Conclusão da Renovação <i>Online</i> dos Empréstimos</h2>
	
	<div class="descricaoOperacao" style="width:90%;">
		<p>A renovação dos empréstimos abaixo foi concluída com sucesso. Um email de confirmação foi enviado para a sua caixa postal.</p>
		<p><strong>Importante: </strong>Para evitar transtornos guarde ou imprima o comprovante de renovação ou uma cópia dessa página até que se tenha certeza que os empréstimos foram renovados. 
		Para verificar as renovações, emita o seu histórico de empréstimos ou verifique o recebimento do email de confirmação da renovação.</p>
		<br/>
		<p><strong>Observação: </strong> Não serão aceitas reclamações posteriores caso não se tenha o comprovante da renovação.</p>
	</div>
	
	<a4j:keepAlive beanName="meusEmprestimosBibliotecaMBean" />

	<%-- Passa alguns parâmetros no request para depois ter como verificar nos log por onde o usuário passou se ele vim afirmar que renovou os emprétimos --%>
	<h:inputHidden id="inputHiddenMensagemPaginaConfirma" value="!!! USUÁRIO SUBMETEU A PÁGINA DE CONCLUSÃO DAS RENOVAÇÕES DOS EMPRÉSTIMOS !!!"></h:inputHidden>
	<h:inputHidden id="idsEmprestimosRenovados" value="#{meusEmprestimosBibliotecaMBean.idsEmprestimoRenovados}"></h:inputHidden>

		<%--  Parte onde o usuário visualiza o comprovante da renovação  --%>
		<c:if test="${meusEmprestimosBibliotecaMBean.habilitarComprovante}">
		
			
				<table  class="subFormulario" align="center">
					<caption style="text-align: center;">Impressão Comprovante</caption>
					<tr>
					<td width="8%" valign="middle" align="center">
						<html:img page="/img/warning.gif"/>
					</td>
					<td valign="middle" style="text-align: justify">
						Por favor, para uma maior segurança imprima o comprovante da renovação clicando no link ao lado.
					</td>
					<td>
						<table>
							<tr>
								<td align="center">
							 		<h:graphicImage url="/img/printer_ok.png" />
							 	</td>
							 </tr>
							 <tr>
							 	<td style="font-size: medium;">
							 		<h:commandLink title="Imprimir Comprovante"  target="_blank" value="COMPROVANTE" action="#{meusEmprestimosBibliotecaMBean.geraComprovanteRenovacao}"  />
							 	</td>
							 </tr>
						</table>
					</td>
					</tr>
				</table>
			<br/>
			
			
			
			
			<div id="divComprovanteRenovacao" style="width: 80%; margin-left: auto; margin-right: auto; margin-top: 20px; border: 1px black solid;">
				
				<h3 style="text-align: center;  margin-top: 20px;">COMPROVANTE DE RENOVAÇÃO</h3>
				
				<div id="divDadosUsuario">
			
					<c:if test="${meusEmprestimosBibliotecaMBean.infoUsuario != null }">
						
						<c:set var="_infoUsuarioCirculacao" value="${meusEmprestimosBibliotecaMBean.infoUsuario}" scope="request" />
						<c:set var="_transparente" value="true" scope="request" />
						<c:set var="_mostrarFoto" value="false" scope="request" />			
									
						<%@include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%>
						
					</c:if>
			
				</div>
				
				
				<div id="divMensagemDeclaracao" style="text-align: center; margin-top: 20px; margin-bottom: 30px;">
					As renovações dos materiais abaixo foram realizadas com sucesso:
				</div>	
				
				<div id="dadosMaterial">
		
					<table style="width: 100%;"> 
		
						<thead style="background-color: transparent;">
							<tr>
								<td style="width: 20%; font-weight: bold; text-align: left;">Data da Renovação</td>
								<td style="width: 60%; font-weight: bold; text-align: center;">Informações do Material</td>
								<td style="width: 20%; font-weight: bold; text-align: left;">Prazo para Devolução</td>
							</tr>
						</thead>
					
						<c:forEach var="operacaoRenovacao" items="#{meusEmprestimosBibliotecaMBean.emprestimosRenovadosOp}" varStatus="status">
							<tr>
								<td style="text-align: left;"> ${operacaoRenovacao.dataRealizacaoFormatada} </td>
								<td style="text-align: left; padding-left: 10px;"> ${operacaoRenovacao.infoMaterial} </td>
								<td style="text-align: left;"> ${operacaoRenovacao.prazoFormatado} </td>
							</tr>
							
						</c:forEach>
					</table>
				
				</div>
			
			
				<div id="divCodigoVerificacao" style="margin-top: 30px;  margin-bottom: 30px; text-align: center; color: gray; font-style: italic;">
						Código de Autenticação : ${meusEmprestimosBibliotecaMBean.codigoAutenticacaoRenovacao}
				</div>
				
			</div>
		
		</c:if>	

	</h:form>

</f:view>


<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>