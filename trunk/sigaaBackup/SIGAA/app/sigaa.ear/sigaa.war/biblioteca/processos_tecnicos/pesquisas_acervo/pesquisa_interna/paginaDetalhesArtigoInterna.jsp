<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<%-- Página de detalhes de um artigo chamada a partir da busca interna de artigos no acervo 

<h2> <ufrn:subSistema /> &gt; Detalhes do Artigo </h2> 

<f:view>

	<a4j:keepAlive beanName="detalhesArtigosInternaMBean"></a4j:keepAlive>

	<a4j:keepAlive beanName="pesquisaInternaBibliotecaMBean"></a4j:keepAlive>

	<table class="visualizacao" style="margin-bottom: 10px; width: 95%"> 
		
		<c:if test="${detalhesArtigosInternaMBean.assinatura != null}">
			<caption>  ${detalhesArtigosInternaMBean.assinatura.titulo} </caption>
		</c:if>
		
		<c:if test="${detalhesArtigosInternaMBean.assinatura == null}">
			<caption>  Dados do Artigo </caption>
		</c:if>
			
			<tr>
			
				<th >Biblioteca:</th>
				<td> ${detalhesArtigosInternaMBean.fasciculoSelecionado.biblioteca.descricao}</td>
				
				<th >Localização:</th>	
				<td style="color:#D99C00"> ${detalhesArtigosInternaMBean.fasciculoSelecionado.numeroChamada}</td>
				
				
				<th>Situação:</th>
				<c:if test="${detalhesArtigosInternaMBean.fasciculoSelecionado.disponivel}">
					<td style="color:green"> ${detalhesArtigosInternaMBean.fasciculoSelecionado.situacao.descricao}</td>
				</c:if>
				
				<c:if test="${! detalhesArtigosInternaMBean.fasciculoSelecionado.disponivel && ! detalhesArtigosInternaMBean.fasciculoSelecionado.emprestado}">
					<td> ${detalhesArtigosInternaMBean.fasciculoSelecionado.situacao.descricao}</td>
				</c:if>
				
				<c:if test="${detalhesArtigosInternaMBean.fasciculoSelecionado.emprestado}"> 
					<td style="color:red"> ${detalhesArtigosInternaMBean.fasciculoSelecionado.situacao.descricao}</td>
				</c:if>
				
			</tr>
			
			<tr>
			
				<th> Ano Cronológico: </th>
				<td> ${detalhesArtigosInternaMBean.fasciculoSelecionado.anoCronologico} </td>
			
				<th> Dia/Mês: </th>	
				<td> ${detalhesArtigosInternaMBean.fasciculoSelecionado.diaMes} </td>
				
				<th> Ano: </th>	
				<td> ${detalhesArtigosInternaMBean.fasciculoSelecionado.ano} </td>
			
			</tr>
			
			
			
			<tr>	
			
				<th> Volume: </th>	
				<td> ${detalhesArtigosInternaMBean.fasciculoSelecionado.volume} </td>
				
				<th > Número: </th>
				<td> ${detalhesArtigosInternaMBean.fasciculoSelecionado.numero} </td>
				
				<th > Edição: </th>
				<td> ${detalhesArtigosInternaMBean.fasciculoSelecionado.edicao} </td>
							
			</tr>
			
			
			
			<tr>
				<td colspan="16">
					
					
					<table class="subFormulario"> 
		
						<caption style="text-align: center"> Informações do Artigo </caption>
							
						<thead>
							<tr align="center">
								<th width="30%" style="text-align: left">Título</th>
								<th width="30%" style="text-align: left">Autor</th>
								<th width="10%"  style="text-align: left">Intervalo de Páginas</th>
								<th width="30%" style="text-align: left">Palavras Chave</th>
							</tr>
						</thead>
						
						<tbody>
						
							<tr>
								<td> ${detalhesArtigosInternaMBean.artigo.titulo} </td>
								<td>
									${detalhesArtigosInternaMBean.artigo.autor}
									<c:if test="${ not empty detalhesArtigosInternaMBean.artigo.autoresSecundarios }">
										<p style="margin-top: 5px;">
										<span style="font-weight: bold;">Autores Secundários:</span>
										<c:forEach var="autorSecundario" items="#{detalhesArtigosInternaMBean.artigo.autoresSecundariosFormatados}">
											<br/> <h:outputText value="#{autorSecundario}" />
										</c:forEach>
										</p>
									</c:if>
								</td>
								<td> ${detalhesArtigosInternaMBean.artigo.intervaloPaginas}</td>
								<td> 
									<table width="100%" id="tabelaInterna">
									<c:forEach items="${detalhesArtigosInternaMBean.artigo.assuntosFormatados}" var="assunto">
										<tr>
											<td>
												${assunto}
											</td>
										</tr>
									</c:forEach>
								</table>
								</td>
							</tr>
							
							<tr>
								<td colspan="16">
									<table class="subFormulario" width="100%">
										<thead>
											<tr align="center">
												<th style="text-align: left">Resumo</th>
											</tr>
										</thead>
										<tbody>
											<tr>
												<td> ${detalhesArtigosInternaMBean.artigo.resumo}</td>
											</tr>	
										</tbody>
									</table>
								<td>
							</tr>
				
						</tbody>
				
					</table>
					
				</td>
			</tr>
		
	</table>
	
	<div style="width: 100%; text-align:center; margin-top:20px">
			<input type="button" id="linkVotarTelaAnterior" value="<< Voltar à Tela de Busca" onclick="javascript:history.go(-1)" />
			<%-- <h:commandButton id="cmdVotarTelaAnterior" value=" << Voltar à Tela de Busca"  action="#{pesquisaInternaBibliotecaMBean.telaBuscaPublicaAcervo}" 
	</div>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp" %> --%>