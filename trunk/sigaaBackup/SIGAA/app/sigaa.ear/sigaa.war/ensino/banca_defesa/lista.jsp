
	<div class="infoAltRem">
		<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Banca
		<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Banca
		<h:graphicImage value="/img/delete_old.gif" style="overflow: visible;" />: Cancelar Banca
	</div>
	
	<table class="listagem">
		<caption>Banca(s) Encontrada(s) (${fn:length(buscaBancaDefesaMBean.listagem)})</caption>
		<thead>
			<tr>
				<th> Discente </th>
				<th>Atividade</th>
				<th style="text-align: center;"> Data </th>
				<th> Status </th>
				<th> </th>
				<th> </th>
				<th> </th>
			</tr>
			<tr>
				<th colspan="4"> Título do Trabalho </th>
				<th></th>
				<th></th>
				<th> </th>
			</tr>
		</thead>
		<tbody>
			<c:set var="grupoPrograma" value="-1" />
			<c:set var="grupoDiscente" value="-1" />
			<c:set var="cursoAnterior" value="-1" />
			<c:set var="index" value="0" />
			<c:forEach items="#{buscaBancaDefesaMBean.listagem}" var="item" varStatus="status">
					
					<c:if test="${item.discente.curso.nomeCompleto != cursoAnterior}">
					<tr>
						<td colspan="7" class="subFormulario">${item.discente.curso.nomeCompleto}</td>
					</tr>
					</c:if>
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td nowrap="nowrap">	
							${item.discente.matriculaNome}
						</td>
						<td>
							<h:outputText value="#{item.matriculaComponente.componenteCodigoNome} (#{item.matriculaComponente.anoPeriodo})" 
							rendered="#{not empty item.matriculaComponente}"/>
							<h:outputText value="--" rendered="#{empty item.matriculaComponente}"/>
						</td>
						<td valign="top" style="text-align: center;">
							<ufrn:format type="dataHora" valor="${item.dataDefesa}"/>
						</td>
						<td>	
							${item.status.descricao}
						</td>
						<td colspan="3"></td>
					</tr>
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td colspan="4">${item.titulo}</td>
						<td align="right" width="20" valign="top">
							<h:commandLink action="#{buscaBancaDefesaMBean.view }" id="linkVisualizar"> 
								<h:graphicImage value="/img/view.gif" title="Visualizar Banca" />
								<f:param name="id" value="#{item.id}"/>
							</h:commandLink>
						</td>
						<td align="right" width="20" valign="top">
							<h:commandLink action="#{buscaBancaDefesaMBean.alterar}" rendered="#{item.ativo}" title="Alterar Banca" id="botaoAlterarBanca">
								<h:graphicImage url="/img/alterar.gif" />
								<f:param name="id" value="#{item.id}"/>
							</h:commandLink>
						</td>				
						<td align="right" width="20" valign="top">
							<h:commandLink action="#{buscaBancaDefesaMBean.remover}" rendered="#{item.ativo}" title="Cancelar Banca" onclick="#{confirmDelete}" id="botaoRemoverBanca">
								<h:graphicImage url="/img/delete_old.gif" />
								<f:param name="id" value="#{item.id}"/>								
							</h:commandLink>
						</td>						
					</tr>
					<c:set var="cursoAnterior" value="${item.discente.curso.nomeCompleto}" />
			</c:forEach>
		</tbody>		
	</table>