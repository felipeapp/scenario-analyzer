<%@include file="../include/cabecalho.jsp"%>

<f:view>
	<div data-role="page" id="page-public-view-processo" data-theme="b">
		<h:form id="form-view-processo-public">
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink action="#{ buscaAcervoTouch.forwardListaAcervo }" id="lnkVoltarBusca">Voltar</h:commandLink></li>
					<li><h:commandLink action="#{ portalPublicoTouch.forwardPortalPublico }" id="lnkInicio">Início</h:commandLink></li>
					<li><h:commandLink id="acessar" action="#{ loginMobileTouch.forwardPaginaLogin }" value="Entrar" /></li>
				</ul>
			</div>
		
			<div data-role="content">
				<p align="center"><strong>Consultar Acervo - Visualização</strong></p>
				<%@include file="/mobile/touch/include/mensagens.jsp"%>			
		
				<strong>Título:</strong>
				${buscaAcervoTouch.obj.titulo }<br />
				<c:if test="${not empty buscaAcervoTouch.obj.subTitulo }">
					<strong>SubTítulo:</strong>
					${buscaAcervoTouch.obj.subTitulo }<br />
				</c:if>
				<strong>Assunto(s):</strong>
				<c:forEach items="${buscaAcervoTouch.obj.assuntosFormatados}" var="assunto">
					${assunto}<br />
				</c:forEach>
				<c:if test="${not empty buscaAcervoTouch.obj.autor }">
					<strong>Autor:</strong>
					${buscaAcervoTouch.obj.autor }<br />
				</c:if>
				<c:if test="${fn:length(buscaAcervoTouch.obj.edicoesFormatadas) > 0 }">	
					<strong>Edição:</strong>
					<c:forEach items="${buscaAcervoTouch.obj.edicoesFormatadas }" var="edicao">
						${edicao}<br />
					</c:forEach>
				</c:if>
				
				<br/>
				
				<c:if test="${!buscaAcervoTouch.periodico }">
					<c:if test="${not empty buscaAcervoTouch.exemplares }">
				   		<p style="text-align: center;"><strong><h:outputText value="Exemplares Encontrados (#{buscaAcervoTouch.qtdTotalExemplares })" escape="false"/></strong></p>					    		
					    <ul data-role="listview" data-inset="true" data-theme="b">
							<c:forEach items="#{buscaAcervoTouch.exemplares }" var="e" varStatus="status">
								<c:if test="${status.index % 25 == 0 && !status.first }">
									<a href="#" class="anchorLink"></a>
								</c:if>
								<li style="font-weight: normal;" data-icon="false">
									<a>
									<label style="white-space: normal;">
										<strong>Código de Barras:</strong>
										${e.codigoBarras }
									</label>
									<br/>
									<label style="white-space: normal;">
										<strong>Tipo:</strong>
										${e.tipoMaterial.descricao}
									</label>
									<br />
									<label style="white-space: normal;">
										<strong>Situação:</strong>
										<c:if test="${e.disponivel}"> 
											<span style="color:green;"> ${e.situacao.descricao }
												<c:if test="${not empty e.prazoConcluirReserva }"> 
													&nbsp&nbsp&nbsp [Reservado, previsão conclusão: <ufrn:format type="data" valor="${e.prazoConcluirReserva }"/> ]
												</c:if>
											</span>		
										</c:if>
										<c:if test="${!e.disponivel && !e.emprestado }"> 
											<td colspan="2"> ${e.situacao.descricao }</td>
										</c:if>
										<c:if test="${e.emprestado }"> 
											<span style="color:red;"> 
										    	 ${e.situacao.descricao } &nbsp&nbsp&nbsp [ Prazo de Devolução: <ufrn:format type="dataHora" valor="${e.prazoEmprestimo }"/> ]	
										 	</span>
										</c:if>
									</label>
									<br/>
									<label style="white-space: normal;">
										<strong>Localização:</strong>
										${e.biblioteca.descricao} <br />
										${e.numeroChamada }
										<c:if test="${not empty e.segundaLocalizacao }">
											<i>&nbsp;&nbsp;&nbsp; ${e.segundaLocalizacao}</i>
										</c:if>
									</label>
									</a>
								</li>
							</c:forEach>
							<c:if test="${buscaAcervoTouch.materialComMaisExemplares }">
								<li>
				          				<h:commandLink value="Carregar mais..." action="#{buscaAcervoTouch.carregarMaisExemplares }" />
								</li>
							</c:if>
						</ul>
					</c:if>
					<c:if test="${empty buscaAcervoTouch.exemplares }">
						<center>Nenhum exemplar encontrado.</center>
					</c:if>
				</c:if>
				
				<c:if test="${buscaAcervoTouch.periodico }">
					<c:if test="${not empty buscaAcervoTouch.fasciculos }">
				   		<p style="text-align: center;"><strong><h:outputText value="Fascículos Encontrados (#{buscaAcervoTouch.qtdTotalExemplares })" escape="false"/></strong></p>					    		
					    <ul data-role="listview" data-inset="true" data-theme="b">
							<c:forEach items="#{buscaAcervoTouch.fasciculos }" var="f" varStatus="status">
								<c:if test="${status.index % 25 == 0 && !status.first }">
									<a href="#" class="anchorLink"></a>
								</c:if>
							<li style="font-weight: normal;" data-icon="false">
									<a>
									<label style="white-space: normal;">
										<strong>Código de Barras:</strong>
										${f.codigoBarras }
									
										<br/>
										<strong>Tipo:</strong>
										${f.tipoMaterial.descricao }<br />
										<strong>Situação:</strong>
										<c:if test="${f.disponivel }"> 
											<span style="color:green;"> ${f.situacao.descricao }
												<c:if test="${not empty f.prazoConcluirReserva }"> 
														&nbsp&nbsp&nbsp [Reservado, previsão conclusão: <ufrn:format type="data" valor="${f.prazoConcluirReserva }"/> ]
												</c:if>
											</span>		
										</c:if>
										<c:if test="${!f.disponivel && !f.emprestado }"> 
											<td colspan="2"> ${f.situacao.descricao }</td>
										</c:if>
										<c:if test="${f.emprestado }"> 
										<span style="color:red;"> 
										     	${f.situacao.descricao } &nbsp&nbsp&nbsp [ Prazo de Devolução: <ufrn:format type="dataHora" valor="${f.prazoEmprestimo }"/> ]	
										 	</span>
										</c:if>
										<br />
										<strong>Volume:</strong>
										${f.volume }<br />
										<strong>Número:</strong>
										${f.numero }<br />
										<strong>Localização:</strong>
										${f.biblioteca.descricao} <br />
										${f.numeroChamada }
										<c:if test="${not empty f.segundaLocalizacao }">
											<i>&nbsp;&nbsp;&nbsp; ${f.segundaLocalizacao}</i>
										</c:if>
									</label>
									</a>
								</li>
							</c:forEach>
							<c:if test="${buscaAcervoTouch.materialComMaisExemplares }">
								<li>
				          				<h:commandLink value="Carregar mais..." action="#{buscaAcervoTouch.carregarMaisExemplares }" />
								</li>
							</c:if>
						</ul>
					</c:if>
					<c:if test="${empty buscaAcervoTouch.fasciculos }">
						<i>Nenhum fascículo encontrado</i>
					</c:if>
				</c:if>
			</div>
			
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
			
			 <script>
				$("#form-view-processo-public\\:lnkVoltarBusca").attr("data-icon", "back");
				$("#form-view-processo-public\\:lnkInicio").attr("data-icon", "home");
				$("#form-view-processo-public\\:acessar").attr("data-icon", "forward");
			</script>
		</h:form>
		<%@include file="/mobile/touch/include/modo_classico.jsp"%>
	</div>
</f:view>

<script type="text/javascript">
	$(document).ready(function(){
		$('html, body').animate({
		    scrollTop: $(".anchorLink:last").position().top
		}, 300);
	});
</script>

<%@include file="../include/rodape.jsp"%>