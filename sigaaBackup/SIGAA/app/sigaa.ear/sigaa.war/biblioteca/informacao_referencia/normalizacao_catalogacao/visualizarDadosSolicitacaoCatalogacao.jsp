<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.academico.dominio.NivelEnsino"%>
<style>
	p{
		 text-indent: 2em;
	}
</style>

<script type="text/javascript">
 
function textCounter(field, idMostraQuantidadeUsuario, maxlimit) {
	
	if (field.value.length > maxlimit){
		field.value = field.value.substring(0, maxlimit);
	}else{ 
		document.getElementById(idMostraQuantidadeUsuario).innerHTML = maxlimit - field.value.length ;
	} 
}

</script>

<f:view>
<h2> <ufrn:subSistema/> &gt; Solicitação de Catalogação na Fonte &gt; Visualizar Dados Solicitação</h2>

<c:if test="${solicitacaoCatalogacaoMBean.obj.atendido}">
	<div class="descricaoOperacao">
		<p>Caro usuário,</p>
		<p>caso algum problema tenha sido encontrado na ficha catalográfica gerada, favor utilizar a opção <strong>Reportar Problema na Catalogação</strong> 
		para que o bibliotecário seja informado e possa corrigi-la. ATENÇÃO, esta operação poderá ser realizada <strong>apenas uma vez</strong>, 
		portanto verifique atentamente o texto.</p>
		<p><strong>Observação: O formato do arquivo da ficha catalográfica gerado depende da forma que o bibliotecário atendeu a solicitação.</strong>
			</p>
	</div>
</c:if>

<a4j:keepAlive beanName="solicitacaoCatalogacaoMBean"></a4j:keepAlive>

<h:form>
	<%-- Parte onde o usuário visualiza o comprovante da solicitação --%>
	<c:if test="${! solicitacaoCatalogacaoMBean.obj.atendido && !solicitacaoCatalogacaoMBean.obj.atendidoFinalizado}">
<%-- 		<h:form> --%>
			<table  class="subFormulario" align="center">
				<caption style="text-align: center;">Informação Importante</caption>
				<tr>
				<td width="8%" valign="middle" align="center">
					<html:img page="/img/warning.gif"/>
				</td>
				<td valign="middle" style="text-align: justify">
					Por favor imprima o comprovante clicando no ícone ao lado para maior segurança dessa operação.
				</td>
				<td>
					<table>
						<tr>
							<td align="center">
								<h:commandLink title="Imprimir Comprovante"  target="_blank" action="#{solicitacaoCatalogacaoMBean.telaComprovante}" >
						 			<h:graphicImage url="/img/printer_ok.png" />
						 		</h:commandLink>
						 	</td>
						 </tr>
						 <tr>
						 	<td style="font-size: medium;">
						 		<h:commandLink title="Imprimir Comprovante"  target="_blank" value="COMPROVANTE" action="#{solicitacaoCatalogacaoMBean.telaComprovante}"  />
						 	</td>
						 </tr>
					</table>
				</td>
				</tr>
			</table>
		<br/>
<%-- 		</h:form> --%>
	</c:if>


	<table class="visualizacao" width="90%">
			
			<caption>Solicitação de Catalogação na Fonte número ${solicitacaoCatalogacaoMBean.obj.numeroSolicitacao} </caption>
			
			<c:if test="${not empty solicitacaoCatalogacaoMBean.obj.discente}">
				<tr>
					<th width="40%">Solicitante:</th>
					<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.discente}"/></td>
				</tr>
				
				<th>Categoria:</th>
				
				<c:if test="${  ( solicitacaoCatalogacaoMBean.obj.discente.nivel == NivelEnsino.STRICTO)
						|| solicitacaoCatalogacaoMBean.obj.discente.lato 
						|| solicitacaoCatalogacaoMBean.obj.discente.mestrado 
						|| solicitacaoCatalogacaoMBean.obj.discente.doutorado}">
					
					<td>Aluno de Pós-Graduação</td>
				</c:if>
				<c:if test="${ ! ( solicitacaoCatalogacaoMBean.obj.discente.nivel == NivelEnsino.STRICTO)
						&& !solicitacaoCatalogacaoMBean.obj.discente.lato 
						&& !solicitacaoCatalogacaoMBean.obj.discente.mestrado 
						&& !solicitacaoCatalogacaoMBean.obj.discente.doutorado}">
					<td>Aluno de Graduação</td>
				</c:if>
				
				<tr>
					<th>Curso:</th>
					<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.discente.curso}"/></td>
				</tr>
				
			</c:if>
			
			<c:if test="${not empty solicitacaoCatalogacaoMBean.obj.servidor}">
				<tr>
					<th>Solicitante:</th>
					<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.servidor}"/></td>
				</tr>
				<tr>
					<th>Categoria:</th>
					<td>
						<c:if test="${solicitacaoCatalogacaoMBean.obj.servidor.categoria.docente}" >
							Docente
						</c:if>
						<c:if test="${solicitacaoCatalogacaoMBean.obj.servidor.categoria.tecnico}" >
							Técnico Administrativo
						</c:if>
					</td>
				</tr>
				<tr>
					<th>Lotação:</th>
					<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.servidor.unidade}"/></td>
				</tr>
			</c:if>
			
			<tr>
				<th>Telefone:</th>
				<td> ${solicitacaoCatalogacaoMBean.obj.pessoa.telefone} </td>
			</tr>
			
			<tr>
				<th>Celular:</th>
				<td> ${solicitacaoCatalogacaoMBean.obj.pessoa.celular} </td>
			</tr>
			
			<tr>
				<th>Email:</th>
				<td> ${solicitacaoCatalogacaoMBean.obj.pessoa.email} </td>
			</tr>
			
			<tr>
				<th>Data da Solicitação:</th>
				<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.dataCadastro}"/></td>
			</tr>
			
			<tr>
				<th>Situação da Solicitação:</th>
				<td>
					<c:if test="${solicitacaoCatalogacaoMBean.obj.solicitado || solicitacaoCatalogacaoMBean.obj.reenviado}">
						<h:outputText value="#{solicitacaoCatalogacaoMBean.obj.situacao.descricao}"/>
					</c:if>
					<%-- <c:if test="${solicitacaoCatalogacaoMBean.obj.validado}">
						<h:outputText style="color:grey;" value="#{solicitacaoCatalogacaoMBean.obj.situacao.descricao}"/>
					</c:if> --%>
					<c:if test="${solicitacaoCatalogacaoMBean.obj.atendido || solicitacaoCatalogacaoMBean.obj.atendidoFinalizado}">
						<h:outputText style="color:green;"  value="#{solicitacaoCatalogacaoMBean.obj.situacao.descricao}"/>
					</c:if>
					<c:if test="${solicitacaoCatalogacaoMBean.obj.cancelado}">
						<h:outputText style="color:red;"  value="#{solicitacaoCatalogacaoMBean.obj.situacao.descricao}"/>
					</c:if>
				</td>
			</tr>
			
			<tr>
				<th>Biblioteca:</th>
				<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.biblioteca.descricao}"/></td>
			</tr>
			
			<tr>
				<th>Tipo do Documento:</th>
				<td>
					<h:outputText value="#{solicitacaoCatalogacaoMBean.obj.tipoDocumento.denominacao}"/>
					<c:if test="${solicitacaoCatalogacaoMBean.obj.tipoDocumento.tipoDocumentoOutro}">
								(  ${solicitacaoCatalogacaoMBean.obj.outroTipoDocumento}  )
					</c:if>	
				</td>
			</tr>
				
			<tr>
				<th>Documento Enviado pelo Usuário:</th>
				<td>
					<a target="_blank" href="${ configSistema['linkSigaa'] }/sigaa/verProducao?idProducao=${solicitacaoCatalogacaoMBean.obj.idTrabalhoDigitalizado}&key=${ sf:generateArquivoKey(solicitacaoCatalogacaoMBean.obj.idTrabalhoDigitalizado) }">
						<h:graphicImage url="/img/porta_arquivos/icones/doc.png" style="border:none" title="Clique aqui para visualizar o documento em formato digital." /> Visualizar
					</a>
				</td>
			</tr>
			
			<tr>
				<th>Número de folhas</th>
				<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.numeroPaginas}"/></td>
			</tr>
			
			<tr>
				<th>Palavras-chave:</th>
				<td>
					<h:outputText id="palavrasChaves"
						value="#{solicitacaoCatalogacaoMBean.obj.palavrasChaveString}"/>
				</td>
			</tr>
				 
			<%-- <tr>
				<th>Autoriza Descarte?</th>
				<td>
					<h:outputText value="#{solicitacaoCatalogacaoMBean.obj.autorizaDescarte}">
				      <f:converter converterId="convertSimNao"/>
					</h:outputText>
				</td>
			</tr> --%>
				
			<c:if test="${solicitacaoCatalogacaoMBean.obj.atendido || solicitacaoCatalogacaoMBean.obj.atendidoFinalizado}">
				<tr style="margin-top: 20px;">
					<th style="padding-top: 20px;">Atendida por:</th>
					<td style="padding-top: 20px;">
						${solicitacaoCatalogacaoMBean.obj.atendente}
					</td>
				</tr>
				<tr>
					<th>Data do Atendimento:</th>
					<td>
						<ufrn:format type="dataHora" valor="${solicitacaoCatalogacaoMBean.obj.dataAtendimento}"/>
					</td>
				</tr>
			</c:if>
			
			<c:if test="${solicitacaoCatalogacaoMBean.obj.atendido || solicitacaoCatalogacaoMBean.obj.atendidoFinalizado}">
				<%-- O usuário solicitou e o bibliotecário cadastrou a ficha catalográfica --%>
				
				<c:if test="${not empty solicitacaoCatalogacaoMBean.obj.fichaGerada}">
					<tr>
						<td colspan="2">
						
						<table width="100%" class="subFormulario">
						<caption>Ficha Catalográfica Gerada</caption>
						
						<tr><td><br/><br/><br/></td></tr>
						
						<tr><td align="center">
						
							<div>
								<span style="display: block;">${solicitacaoCatalogacaoMBean.tituloFicha}</span>
								<span style="display: block;">Catalogação de Publicação na Fonte. ${solicitacaoCatalogacaoMBean.siglaInstituicao} - ${solicitacaoCatalogacaoMBean.obj.biblioteca.descricao}</span>
							</div>
							<div style="text-align: left; width: 500px; border-width: 1px; border-color: black; border-style: solid; padding: 10px 10px 10px 10px">
								${solicitacaoCatalogacaoMBean.fichaCatalografica}
							</div>
							
<%-- 							<h:form> --%>
								<h:commandLink action="#{solicitacaoCatalogacaoMBean.imprimirFichaCatalograficaPDF}" target="_blank">
									<f:param name="id" value="#{solicitacaoCatalogacaoMBean.obj.id}" />
									<h:graphicImage url="/img/pdf.png" style="border:none" title="Clique aqui para visualizar a ficha catalográfica." /> Imprimir Ficha Catalográfica (.pdf)
								</h:commandLink>
<%-- 							</h:form> --%>
							
							</td></tr>
						
						<%-- <tr><td align="center">
						
							<table width="500px" style="border-width: 1px; border-color: black; border-style: solid;">
								
								<tr>
									<td colspan="2"><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.nomeFormatado}" id="txtTitulo"/> <br/>
									<p><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.titulo}"/>
									/ <h:outputText value="#{solicitacaoCatalogacaoMBean.obj.solicitante}"/>. - ${solicitacaoCatalogacaoMBean.obj.fichaGerada.data}, ${solicitacaoCatalogacaoMBean.obj.fichaGerada.data}</p>
									</td>
								</tr>
								
								<tr>
									<td colspan="2"><p><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.folhas}" id="txtFolhas"/> f.<c:if test="${solicitacaoCatalogacaoMBean.obj.fichaGerada.ilustrado}"> : il.</c:if></p></td>
								</tr>
								
								<tr>
									<td colspan="2"><p>Orientador: <h:outputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.orientador}" id="txtOrientador"/></p></td>
								</tr>
								
								<tr>
									<td colspan="2"><p><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.localFormatado}"id="txtLocal"/></td>
								</tr>
								
								<tr>
									<td colspan="2"><p> <h:outputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.palavrasChaveStringFormatada}"/></p></td>
								</tr>
								
								<tr>
									<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.biblioteca.identificador}"/></td>
									<td align="right">CDU <h:outputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.cdu}" id="txtCdu"/></td>
								</tr>
							</table>
							<h:form>
								<h:commandLink value="Imprimir Ficha Catalográfica" action="#{solicitacaoCatalogacaoMBean.imprimirFichaCatalografica}" target="_blank">
									<f:param name="id" value="#{solicitacaoCatalogacaoMBean.obj.id}" />
								</h:commandLink>
							</h:form>
							
							</td></tr> --%>
							
							<tr><td><br/><br/><br/></td></tr>
							
							</table>
							
						</td>
					</tr>
				</c:if>
				<c:if test="${not empty solicitacaoCatalogacaoMBean.obj.idFichaDigitalizada}">
					<tr>
						<td colspan="2">
						
						<table width="100%" class="subFormulario">
						<caption>Ficha Catalográfica Gerada</caption>
						
						<tr><td><br/><br/><br/></td></tr>
						
						<tr><td align="center">
						
							<%-- <div style="text-align: left; width: 500px; border-width: 1px; border-color: black; border-style: solid; padding: 10px 10px 0px 10px">
								${solicitacaoCatalogacaoMBean.fichaDigitalizada}
							</div> --%>
							
<%-- 							<h:form> --%>
								<a target="_blank" href="${ configSistema['linkSigaa'] }/sigaa/verProducao?idProducao=${solicitacaoCatalogacaoMBean.obj.idFichaDigitalizada}&key=${ sf:generateArquivoKey(solicitacaoCatalogacaoMBean.obj.idFichaDigitalizada) }">
									<h:graphicImage url="/img/porta_arquivos/icones/desconhecido.png" style="border:none" title="Clique aqui para visualizar a ficha catalográfica." /> Imprimir Ficha Catalográfica
								</a>
<%-- 							</h:form> --%>
							
							</td></tr>
							
							<tr><td><br/><br/><br/></td></tr>
							
							</table>
							
						</td>
					</tr>
				</c:if>
			</c:if>
				
			<c:if test="${solicitacaoCatalogacaoMBean.obj.atendido}">
				<tr>
					<td colspan="2">
						<table class="subFormulario" style="width: 100%">
							<caption>Reportar Problema na Catalogação</caption>
							<tr>
								<th class="obrigatorio" style="font-weight: normal; padding-right: 13px; width: 23%">Motivo:</th>
								<td>
									<h:inputTextarea
											id="txtAreaMotivoReenvio"
											value="#{solicitacaoCatalogacaoMBean.motivoReenvio}" 
											cols="80" rows="6"
											onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 400);" />
								</td>
							</tr>
							<tr>
								<th style="font-weight: normal; width: 23%">Caracteres Restantes:</th>
								<td>
									<span id="quantidadeCaracteresDigitados">400</span>/400
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</c:if>
			
			<tfoot>
				<tr>
					<td colspan="2" align="center">
<%-- 						<h:form id="form"> --%>
							<c:if test="${solicitacaoCatalogacaoMBean.obj.atendido}">
								<h:commandButton
										id="confirmButton"
										onclick="return confirm('Esta operação NÃO poderá ser cancelada e só poderá ser realizada UMA VEZ. Deseja continuar?');"
										value="Reportar Problema"
										action="#{solicitacaoCatalogacaoMBean.reenviarSolicitacao}" />
							</c:if>
							
							<h:commandButton value="<< Voltar" action="#{solicitacaoCatalogacaoMBean.verMinhasSolicitacoes}" immediate="true" />
<%-- 						</h:form> --%>
					</td>
				</tr>
			</tfoot>
	</table>

	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>