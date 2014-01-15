<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<c:if test="${bancaPos.portalCoordenadorStricto or acesso.ppg}">
		<h2 class="title"><ufrn:subSistema /> > Validar Bancas Pendentes</h2>
	
		<div class="descricaoOperacao">
			<p>Caro Coordenador,</p>
			<p>Nesta tela será possível analisar e validar as bancas cadastradas por Orientadores de Pós-Graduação.</p>
		</div>
	</c:if>
	<c:if test="${bancaPos.portalDocente}">
		<h2 class="title"><ufrn:subSistema /> > Acompanhar Bancas Pendentes</h2>
		
		<div class="descricaoOperacao">
			<p>Caro Docente,</p>
			<p>Nesta tela serão listadas todas as Bancas Cadastradas que estão pendentes de aprovação.</p>
		</div>			
	</c:if>
	
	<h:form id="lista">

	<div class="infoAltRem">
	    <img src="${ctx}/img/view.gif"/>: Visualizar
		<c:if test="${bancaPos.portalCoordenadorStricto}">    
			<img src="${ctx}/img/delete_old.gif"/>: Cancelar Banca
			<img src="${ctx}/img/check.png"/>: Aprovar Banca	
			<img src="${ctx}/img/listar.gif"/>: Ver Histórico
			<img src="${ctx}/img/prodocente/lattes.gif"/>: Currículo Lattes
		</c:if>		
	</div>

	<table class="listagem">
		<caption> Bancas Encontradas (${fn:length(bancaPos.bancasDoDiscente)})</caption>
		<thead>
			<tr>
				<th> Matrícula </th>
				<th colspan="2"> Nome </th>
				<th> </th>
				<th> </th>
				<th> </th>
				<th> </th>
				<th></th>
				<th></th>
			</tr>
			<tr>
				<th></th>
				<th> Tipo </th>
				<th> Título do Trabalho </th>
				<th style="text-align: center;"> Data </th>
				<th></th>
				<th></th>
				<th></th>
				<th></th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<c:if test="${empty bancaPos.bancasDoDiscente}">
				<tr>
					<td colspan="8" align="center"><i>Nenhuma banca pendente de aprovação foi encontrada.</i></td>
				</tr>
			</c:if>
			
			<c:set var="grupoPrograma" value="-1" />
			<c:set var="grupoDiscente" value="-1" />
			<c:set var="index" value="0" />
			<c:forEach items="#{bancaPos.bancasDoDiscente}" var="item" varStatus="status">
					<c:set var="loopPrograma" value="${item.dadosDefesa.discente.gestoraAcademica.id}"/>
					<c:set var="loopDiscente" value="${item.dadosDefesa.discente.id}"/>
					<c:if test="${grupoPrograma != loopPrograma}">
						<tr>
							<td colspan="9" class="subFormulario">${item.dadosDefesa.discente.gestoraAcademica.nome}</td>
						</tr>
						<c:set var="grupoPrograma" value="${loopPrograma}"/>
						<c:set var="index" value="0" />
					</c:if>
					<c:if test="${grupoDiscente != loopDiscente}">
						<c:set var="index" value="${index + 1}" />
						<tr class="${index % 2 != 0 ? 'linhaPar' : 'linhaImpar' }">
							<td>	
								${item.dadosDefesa.discente.matricula}
							</td>
							<td nowrap="nowrap" colspan="4">	
								${item.dadosDefesa.discente.nome}
								(${item.dadosDefesa.discente.nivelDesc})
							</td>
							<td colspan="4"></td>
						</tr>
						<c:set var="grupoDiscente" value="${loopDiscente}"/>
					</c:if>
					<tr class="${index % 2 != 0 ? 'linhaPar' : 'linhaImpar' }" >
						<td></td>
						<td valign="top">${item.tipoDescricao }</td>
						<td >
							${item.dadosDefesa.tituloStripHtml}
						</td>
						<td valign="top" style="text-align: center;">
							<h:outputText value="#{item.data}" />
						</td>
						<td style="width: 5px">
							<h:commandLink action="#{ consultarDefesaMBean.visualizarDadosDefesa }" id="linkParaVisualizarDadosDefesa"> 
									<f:param name="idBancaPos" value="#{ item.id }" id="paramIdDaBancaPos"/>
									<h:graphicImage value="/img/view.gif" title="Visualizar" />
							</h:commandLink>
						</td>
						<td style="width: 5px">
							<h:commandLink action="#{bancaPos.telaCancelamento}" title="Cancelar Banca" 
								onclick="#{confirmDelete}" id="botaoRemoverBanca" rendered="#{bancaPos.portalCoordenadorStricto or acesso.ppg}">
								<h:graphicImage url="/img/delete_old.gif" />
								<f:param id="idBancaPos" name="idBancaPosRemocao" value="#{item.id}"/>									
							</h:commandLink>						
						</td>
						<td style="width: 5px">
							<h:commandLink action="#{bancaPos.aprovarBanca}" title="Aprovar Banca" id="botaoAprovarBanca"
								 rendered="#{bancaPos.portalCoordenadorStricto or acesso.ppg}">
								<h:graphicImage url="/img/check.png" />
								<f:param name="id" value="#{item.id}"/>									
							</h:commandLink>
						</td>
						<td style="width: 5px">
							<h:commandLink action="#{bancaPos.verHistorico}" title="Ver Histórico" id="botaoVerHistorico"
								 rendered="#{bancaPos.portalCoordenadorStricto or acesso.ppg}">
								<h:graphicImage value="/img/listar.gif"/>
								<f:param name="id" value="#{item.id}"/>								
							</h:commandLink>
						</td>
						<td style="width: 5px" >							
							<h:outputText escape="false" rendered="#{(bancaPos.portalCoordenadorStricto or acesso.ppg) and not empty item.dadosDefesa.discente.discente.pessoa.perfil.enderecoLattes}" value="
								<a href='#{item.dadosDefesa.discente.discente.pessoa.perfil.enderecoLattes}' target='_blank'>
								<img src='/sigaa/img/prodocente/lattes.gif' title='Currículo Lattes'></img> 
							 </a>"/>
						</td>
					</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="9" align="center">
					<h:commandButton value="Cancelar" action="#{bancaPos.cancelar}" onclick="#{confirm}" immediate="true" id="btCancel"/>
				</td>
			</tr>
		</tfoot>
	</table>

	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
