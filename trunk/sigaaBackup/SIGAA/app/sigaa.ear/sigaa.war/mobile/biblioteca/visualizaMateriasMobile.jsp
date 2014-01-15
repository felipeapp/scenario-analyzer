
<%@include file="/mobile/commons/cabecalho.jsp"%>

<f:view>
	
		<h:form>
			
			<h:commandButton value="<< Voltar" action="#{operacoesBibliotecaMobileMBean.telaResultadosConsultaTituloMobile}"/> <br/><br/>
	
		<div> 

		
		<c:if test="${! operacoesBibliotecaMobileMBean.periodico}">

			<table class="listagemMobile" > 
				<caption> Existe(m) ${fn:length( operacoesBibliotecaMobileMBean.exemplares)} exemplar(es) para esse Título </caption>
				
				<tbody>
				
					<c:forEach var="exemplar" items="#{operacoesBibliotecaMobileMBean.exemplares}" varStatus="status">
						
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<th colspan="6"> Código de Barras:</th> 
						</tr>
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<td colspan="6"> ${exemplar.codigoBarras} </td>
						</tr>
						
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<th colspan="6"> Tipo de Material:</th> 
						</tr>
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<td colspan="6"> ${exemplar.tipoMaterial.descricao} </td>
						</tr>
						
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">	
							<th colspan="6"> Biblioteca: </th>
						</tr>
						
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<td colspan="6"> ${exemplar.biblioteca.descricao} </td>
						</tr>
						
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">	
							<th colspan="6"> Localização: </th>
						</tr>
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<td colspan="6" style="color:#D99C00"> ${exemplar.numeroChamada} </td>
						</tr>
						
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">	
							<th colspan="6"> Situação: </th>
						</tr>
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<c:if test="${exemplar.disponivel}"> 
								<td colspan="6" style="color:green"> ${exemplar.situacao.descricao} </td>
							</c:if>
							<c:if test="${! exemplar.disponivel && ! exemplar.emprestado}"> 
								<td colspan="6" style="color:green"> ${exemplar.situacao.descricao} </td>
							</c:if>
							<c:if test="${exemplar.emprestado}"> 
								<td colspan="6" style="color:red"> ${exemplar.situacao.descricao}</td>
							</c:if>
						</tr>
						
						<tr>
							<td colspan="6">
								<hr style="color:#111155"/>
							</td>	
						</tr>
						
					</c:forEach>
	
				</tbody>
	
			</table>
		
		</c:if>
		
		
		<c:if test="${operacoesBibliotecaMobileMBean.periodico}">
			
			<table style="width: 200px;"> 
				<c:if test="${operacoesBibliotecaMobileMBean.assinatura != null}">
					<tr>
						<td colspan="10" style="text-align:center;color: #003395;font-variant: small-caps;font-weight: bold; padding-top:10px">
							<span style="color:black;"> Assinatura:</span> ${operacoesBibliotecaMobileMBean.assinatura.titulo}
						</td>			
					</tr>
				</c:if>
			</table>
			
			<table class="listagemMobile"> 
				<caption>  Exite(m) ${fn:length( operacoesBibliotecaMobileMBean.fasciculos)} fascículo(s) para esse Título </caption>
								
				<tbody>
	   
					<c:forEach var="fasciculo" items="#{operacoesBibliotecaMobileMBean.fasciculos}" varStatus="status">
	
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<th width="10%">Código de Barras:</th>
							<td> ${fasciculo.codigoBarras} </td>
						</tr>	
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<th>Tipo de Material:</th>
							<td> ${fasciculo.tipoMaterial.descricao}</td>
						</tr>	
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<th width="5%"> Ano Cron.: </th>	
							<td width="5%" colspan="1"> ${fasciculo.anoCronologico} </td>
						</tr>		
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<th width="5%"> Ano: </th>	
							<td width="5%" colspan="1"> ${fasciculo.ano} </td>
						</tr>		
						
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<th width="5%"> Volume: </th>	
							<td width="5%" colspan="1"> ${fasciculo.volume} </td>
						</tr>
								
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<th width="5%"> Número: </th>	
							<td width="5%" colspan="1"> ${fasciculo.numero} </td>
						</tr>		
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<th width="5%"> Edição: </th>	
							<td width="5%" colspan="1"> ${fasciculo.edicao} </td>
						</tr>		
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<th>Biblioteca:</th>	
							<td> ${fasciculo.biblioteca.descricao}</td>
						</tr>		
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<th>Localização:</th>			
							<td style="color:#D99C00"> ${fasciculo.numeroChamada}</td>
						</tr>		
						
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<th>Situação:</th>	
							<c:if test="${fasciculo.disponivel}"> 
								<td style="color:green"> ${fasciculo.situacao.descricao}</td>
							</c:if>
							<c:if test="${! fasciculo.disponivel && ! fasciculo.emprestado}"> 
								<td> ${fasciculo.situacao.descricao}</td>
							</c:if>
							<c:if test="${fasciculo.emprestado}"> 
								<td style="color:red"> ${fasciculo.situacao.descricao}</td>
							</c:if>
							
							<%-- <td> ${fasciculo.quantidadeArtigos}</td>
							
							<c:if test="${fasciculo.quantidadeArtigos > 0}">
								 
								<td>
								 	<h:form>
										<h:commandLink action="#{operacoesBibliotecaMobileMBean.telaInformacoesArtigoPublica}">
											<h:graphicImage url="/img/view.gif" style="border:none"
												title="Clique aqui para visualizar os artigos desse fascículo" />
	
											<f:param name="idFasciculoDosArtigos" value="#{fasciculo.id}"/>					
										</h:commandLink>
									</h:form>
								</td>
								
							</c:if>  
							
							<c:if test="${fasciculo.quantidadeArtigos <= 0}">
								<td>
									<h:graphicImage url="/img/view.gif" style="border:none"
										title="Fascículo não possui artigos" />
								</td>
							</c:if>  --%>
							
						</tr>
						
						<tr>
							<td colspan="6">
								<hr style="color:#111155"/>
							</td>	
						</tr>
						
					</c:forEach>
	
				</tbody>
	
			</table>
		
		</c:if>

		</small>

	</div>
			
		</h:form>
		
	</f:view>



<%@include file="/mobile/commons/rodape.jsp" %>