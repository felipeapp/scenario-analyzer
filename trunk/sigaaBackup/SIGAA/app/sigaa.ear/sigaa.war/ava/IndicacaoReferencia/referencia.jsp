<h:messages showDetail="true" />

<style>
		.descricao { padding-left : 10px; }
		.listing { border: 1px solid #666666; margin: 10px auto; margin-top: 0; width: 95%; border-collapse: collapse; }
</style>		

<fieldset>
	<legend>Visualização de Referência</legend>
	
	
<table>
	<tr><th style="width:130px;height:20px;vertical-align:top;"><b>Descrição:</b> 
		</th><td style="vertical-align:top;">
			<span style="padding-left:20px;">	 
				<h:outputText id="descricao" value="#{indicacaoReferencia.object.descricao}"/>
			</span>
		</td></tr>
	
	<tr><th style="width:130px;height:20px;vertical-align:top;"><b>Tipo:</b> 
		</th><td style="vertical-align:top;">
			<span style="padding-left:20px;">	
				<h:outputText id="tipo" value="#{indicacaoReferencia.object.tipoDesc}"/>				
			</span>
		</td></tr>
		
	<c:if test="${ indicacaoReferencia.object.artigo || indicacaoReferencia.object.revista }">
		<tr><th style="width:130px;height:20px;vertical-align:top;"><b>Autor:</b> 
			</th><td style="vertical-align:top;">
					<div style="padding-left:20px;" >
						${indicacaoReferencia.object.autor}
					</div>
			</td>
		</tr>
		
		<tr><th style="width:130px;height:20px;vertical-align:top;"><b>Ano:</b> 
			</th><td style="vertical-align:top;">
					<div style="padding-left:20px;" >
						${indicacaoReferencia.object.ano}
					</div>
			</td>
		</tr>
		
		<c:if test="${ indicacaoReferencia.object.revista }">
			<tr><th style="width:130px;height:20px;vertical-align:top;"><b>Editora:</b> 
				</th><td style="vertical-align:top;">
						<div style="padding-left:20px;" >
							${indicacaoReferencia.object.editora}
						</div>
				</td>
			</tr>
			
			<tr><th style="width:130px;height:20px;vertical-align:top;"><b>Edição:</b> 
				</th><td style="vertical-align:top;">
						<div style="padding-left:20px;" >
							${indicacaoReferencia.object.edicao}
						</div>
				</td>
			</tr>
		</c:if>
	</c:if>
	
	<tr><th style="width:130px;height:20px;vertical-align:top;"><b>URL:</b> 
		</th><td style="vertical-align:top;">
			<span style="padding-left:20px;">	
				<h:outputText id="url" value="#{indicacaoReferencia.object.url}"/>	
			</span>
		</td></tr>
	
	<tr><th style="width:130px;height:20px;vertical-align:top;"><b>Detalhes:</b> 
		</th><td style="vertical-align:top;">
				<div style="padding-left:20px;" >
					${indicacaoReferencia.object.detalhes}
				</div>
		</td>
	</tr>

	<c:if test="${not empty indicacaoReferencia.object.aula}">
		<tr><th style="width:130px;height:30px;vertical-align:top;"><b>Tópico de Aula:</b> 
			</th><td style="vertical-align:top;">
					<div style="padding-left:20px;" >
						${indicacaoReferencia.object.aula.descricao}
					</div>
			</td></tr>
	</c:if>
</table>

<ul class="form">
	<c:if test="${ indicacaoReferencia.object.livro && not empty indicacaoReferencia.object.tituloCatalografico }">
		<li>		
			<table class="formulario" style="width:80%;">
				<caption>Livro</caption>
				<tbody><tr><td>
					<table class="subformulario" style="width:100%;">
					<caption>Informações:</caption>
					<tbody><tr><td>
						<table style="border: 1px solid #666666;width:100%;">
						<thead>
							<tr>
								<th><p align="left">Autor</p></th>
								<th><p align="left">Título</p></th>
								<th><p align="left">Edição</p></th>
								<th><p align="left">Ano</p></th>
								<th><p align="left">Qtd</p></th>
							</tr>
						</thead>
						<tbody>
							<c:set var="tituloCache" value="#{ indicacaoReferencia.tituloCatalografico }" />
							<tr>
								<td><h:outputText id="autor" value="#{ tituloCache.autor }"/></td>
								<td><h:outputText id="titulo" value="#{ tituloCache.titulo }"/></td>
								<td><h:outputText id="edicao" value="#{ tituloCache.edicao }"/></td>
								<td><h:outputText id="ano" value="#{ tituloCache.ano }"/></td>
								<td><h:outputText id="quantidade" value="#{ tituloCache.quantidadeMateriaisAtivosTitulo }"/></td>
							</tr>
			
						</tbody>
					</table>	
				</td></tr></tbody>
				</table>
	
				<tr><td>
					<table class="subformulario" style="width:100%;">
					<caption>Exemplares Disponíveis:</caption>
					<tbody>
							<tr><td>
							<table style="border: 1px solid #666666;width:100%;">
								<thead>
									<tr>
										<th><p align="left">Biblioteca</p></th>
										<th><p align="left">Código de Barras</p></th>
										<th><p align="left">Tipo Material</p></th>
										<th><p align="left">Coleção</p></th>
										<th><p align="left">Status</p></th>
										<th><p align="left">Situação</p></th>
									</tr>
								</thead>
								<tBody>
									<c:forEach var="exemplar" items="#{indicacaoReferencia.exemplares}" varStatus="loop">
										<tr>
											<td><h:outputText id="biblioteca" value="#{exemplar.biblioteca.descricao}"/></td>
											<td><h:outputText id="codBarras" value="#{exemplar.codigoBarras}"/></td>
											<td><h:outputText id="tipoMaterial" value="#{exemplar.tipoMaterial.descricao}"/></td>
											<td><h:outputText id="colecao" value="#{exemplar.colecao.descricao}"/></td>
											<td><h:outputText id="status" value="#{exemplar.status.descricao}"/></td>
											<c:if test="${exemplar.emprestado}">
												<td style="color:red;">
													<h:outputText id="situacaoEmprestado" value="#{exemplar.situacao.descricao}"/>
													<ufrn:format type="dataHora" valor="${exemplar.prazoEmprestimo}"/> 
												</td>
											</c:if>
											<c:if test="${exemplar.disponivel}">
												<td style="color:green;"><h:outputText id="situacaoDisponivel" value="#{exemplar.situacao.descricao}"/></td>
											</c:if>
											<c:if test="${!exemplar.disponivel && !exemplar.emprestado}">
												<td><h:outputText id="situacao" value="#{exemplar.situacao.descricao}"/></td>
											</c:if>
										</tr>
									</c:forEach>
								</tBody>
							</table>
						
							</td></tr>
							</table>
					</td></tr>
					</tbody>
					</table>	
		</li>
	</c:if>
</ul>

<div class="botoes-show" align="center">

	<input type="hidden" name="id" value="${ indicacaoReferencia.object.id }"/>

	<c:if test="${ turmaVirtual.docente }">
		<h:commandButton action="#{indicacaoReferencia.editar}" value="Editar"/>
	</c:if>	
	
	<c:if test="${ turmaVirtual.docente || turmaVirtual.discente }">
		<h:commandButton action="#{indicacaoReferencia.voltarAnterior}" value="<< Voltar"/>
	</c:if>
	
	<c:if test="${ !turmaVirtual.docente && !turmaVirtual.discente }">
	<a href="javascript:history.go(-1)">Voltar</a>
	</c:if>
</div>

</fieldset>
