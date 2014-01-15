<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" type="text/css" href="/sigaa/css/ensino/detalhes_discente.css"/>
<script type="text/javascript" src="/sigaa/javascript/graduacao/busca_discente.js"> </script>
<style>
	dl { margin-left: 3em; }
	dt { font-weight: bold; }
	dd { margin-left: 10px; margin-bottom: 7px;}
</style>

<c:set var="hideSubsistema" value="true" />

<f:view>
		<h2><ufrn:subSistema /> &gt; Todas as Comunidades Virtuais que você está associado </h2>
	<h:form>
		<div class="descricaoOperacao">
			<p>
				Caro Usuário, 
			</p>
			<br/>
			<p>
				A <b>Comunidade Virtual</b> é um ambiente que proporciona a socialização e interação virtual aos usuários do nosso sistema acadêmico. Ela se assemelha
				ao <em>Ambiente Virtual de Aprendizado</em> no sentido de permitir compartilhar informações, disponibilizar fóruns, download de arquivos, enquentes, notícias 
				e chats para os seus participantes.
			</p>
			<p>
				É possível criar várias comunidades sobre os temas que lhe sejam convenientes e deixá-las públicas a qualquer usuário do sistema ou restrita
				a um grupo de convidados, tudo isso de acordo com sua necessidade.
			</p>
			<p>
				Veja abaixo os tipos de comunidades virtuais disponíveis:
			</p>
			<dl>
				<dt> Privada  </dt>
					<dd> Apenas os moderadores podem convidar membros à comunidade. Comunidades privadas não serão listadas na busca de comunidades virtuais. </dd>
					
					<dt> Pública e Não Moderada </dt> 
						<dd> Qualquer usuário pode inscrever-se na comunidade, sem a necessidade de solicitar permissão para tal. </dd>
					<dt> Moderada
						<dd> A comunidade será listada nas buscas mas é necessário que os usuários solicitem participação na comunidade aos moderadores. </dd>		
				<c:if test="${comunidadeVirtualMBean.gestorComunidades}"> 
					<dt> Restrito a Grupos
						<dd> A comunidade será criada para um grupo personalizado de usuários. Seu acesso na busca de comunidades é público
							mas somente os pertencentes aquele grupo podem ingressar nela. 	</dd>
				</c:if>
			</dl>
		</div>
		<div class="infoAltRem">
			<h:commandLink id="buscarComunidadesVirtuais" actionListener="#{ buscarComunidadeVirtualMBean.criar }">
				<h:graphicImage value="/img/buscar.gif" alt="Buscar Comunidades Virtuais" style="overflow: visible;"/>Buscar Comunidades Virtuais
			</h:commandLink> <br>
			<h:commandLink id="criarComunidadeVirtual" actionListener="#{ comunidadeVirtualMBean.criar }" rendered="#{ comunidadeVirtualMBean.servidor }">
				<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" alt="Criar Comunidade Virtual"/>Criar Comunidade Virtual 
			</h:commandLink>
		</div>
		<c:if test="${not empty buscarComunidadeVirtualMBean.comunidadesPorPessoa}">
				<table class="listagem">
					<caption>Comunidades localizadas</caption>
					<thead>
						<tr>
							<th>Nome da comunidade</th>
							<th>Tipo da comunidade</th>
							<th></th>
							<th></th>
							<th></th>
						</tr>
					</thead>
					<tbody>
					<c:forEach items="#{buscarComunidadeVirtualMBean.comunidadesPorPessoa}" var="cv">
						<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">						
							<c:if test="${cv.ativa == true}">
								<td>
									<h:commandLink id="nomeComunidade" action="#{comunidadeVirtualMBean.entrar}" value="#{cv.nome}">
										<f:param value="#{cv.id}" name="idComunidade" />
									</h:commandLink>
								</td>
								<td>
									${cv.tipoComunidadeVirtual.descricao}
								</td>
							</c:if>							
						</tr>
					</c:forEach>	
					</tbody>
					<tfoot>
					<tr>
						<td colspan="6" style="text-align: center; font-weight: bold;">
							${fn:length(buscarComunidadeVirtualMBean.comunidadesPorPessoa)} comunidade(s) encontrada(s)
						</td>
					</tr>
					</tfoot>
				</table>
		</c:if>
		
		<c:set var="turmasVirtuais" value="${ portalDiscente.turmasVirtuaisHabilitadas }"/>
		<c:if test="${!empty turmasVirtuais}">
		
			<div class="descricaoOperacao">
				<p>Prezado(a) usuário(a), segue a lista das turmas virtuais nas quais você tem permissão de acesso.</p>
			</div>
		
			<div id="turmas-habilitadas" class="simple-panel">
				<c:if test="${ portalDiscente.modoReduzido }">
					<p class="vazio">
						Listagem de turmas virtuais habilitadas temporariamente indisponível. Para acessá-las, <a href="${ctx}/portais/discente/turmas_habilitadas.jsf">clique aqui</a>.
					</p>
				</c:if>
				<c:if test="${ not portalDiscente.modoReduzido }">
					<table class="listagem">
						<caption>Turmas Virtuais Habilitadas</caption>
						<thead>
							<tr>
								<th>Disciplina</th>
								<th style="text-align:center;"">Créditos</th>
								<th style="text-align:center;">Horário</th>
							</tr>
						</thead>
						<tbody>
							<c:forEach items="#{portalDiscente.turmasVirtuaisHabilitadas}" var="t" varStatus="status">
								<tr class="${status.index % 2 == 0 ? "odd" : "" }">
									<td class="descricao">
										<h:commandLink id="turmasVirtuaisHabilitadas" action="#{turmaVirtual.entrar}" value="#{t.anoPeriodo} - #{t.disciplina.nome} - T#{t.codigo}">
											<f:param name="idTurma" value="#{t.id}" />
										</h:commandLink>
									</td>
									<td style="text-align:center;""><center>${t.disciplina.crTotal}</center></td>
									<td style="text-align:center; width="35%"><center>${t.descricaoHorario}</center></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:if>
			</div>
		</c:if>
	</h:form>
</f:view>

<div class="linkRodape">
	<html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
