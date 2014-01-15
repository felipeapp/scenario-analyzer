<style>
	#panelPerfilContentDiv {
		text-align:left;
	}
	
	#panelPerfilContentDiv h1 {
		font-size:9pt;
		font-weight:bold;
		padding:3px;
		background:#DFE8F6;
		margin-top:20px;
		margin-bottom:10px;
	}
</style>

<%-- Botão que exibe o painel do perfil --%>
<input type="submit" id="bVisualizarPerfil" style="display:none;" onclick="dialogPerfil.show();return false;"/>

<%-- Painel que exibe o perfil do discente selecionado --%>
<p:dialog header="Perfil do Discente" widgetVar="dialogPerfil" width="530" height="400">

	<a4j:outputPanel id="basePanelPerfil">
		<c:set var="discenteSelecionado" value="#{turmaVirtual.matriculaSelecionada}" />
		
		<h:outputText value="O usuário selecionado não é um discente." rendered="#{discenteSelecionado == null}" />
		
		<a4j:outputPanel rendered="#{discenteSelecionado != null}">
			<div style="width:100%;overflow:auto;height:330px;">
				<table style="width:95%;"><tr>
					<td style="vertical-align:top;width:57px;">
						<h:graphicImage value="/verFoto?idFoto=#{discenteSelecionado.discente.idFoto}&key=#{ sf:generateArquivoKey(discenteSelecionado.discente.idFoto) }" width="48" height="60" rendered="#{discenteSelecionado.discente.idFoto != null}" />
						<h:graphicImage value="/img/no_picture.png" width="48" height="60" rendered="#{discenteSelecionado.discente.idFoto == null}" />
					</td>
					<td style="vertical-align:top;line-height:150%;">
						<h:graphicImage value="/img/portal_turma/online.png" title="Usuário On-Line no SIGAA" rendered="#{discenteSelecionado.discente.usuario.online && !turmaVirtual.imprimir}" />
						<h:graphicImage value="/img/portal_turma/offline.png" title="Usuário Off-Line no SIGAA" rendered="#{!discenteSelecionado.discente.usuario.online && !turmaVirtual.imprimir}" />
						
						<h:outputText value="#{discenteSelecionado.discente.pessoa.nome}" style="font-weight:bold;" />
						<h:outputText value="(#{discenteSelecionado.situacaoMatricula.descricao})" styleClass="situacao" rendered="#{discenteSelecionado.trancado or discenteSelecionado.cancelada}" />
						
						<h1>Dados do Discente</h1>
						
						<strong style="font-weight:bold;">Curso:</strong> <em><h:outputText value="#{discenteSelecionado.discente.curso.descricao}" /></em><br/>
						<strong style="font-weight:bold;">Matrícula:</strong> <em><h:outputText value="#{discenteSelecionado.discente.matricula}" /></em><br/>
						<strong style="font-weight:bold;">Usuário:</strong> <em><h:outputText value="#{ discenteSelecionado.discente.usuario != null ? discenteSelecionado.discente.usuario.login : 'Sem cadastro no sistema' }" /></em><br/>
						<strong style="font-weight:bold;">E-mail:</strong> <em><h:outputText value="#{ discenteSelecionado.discente.pessoa.email != null ? discenteSelecionado.discente.pessoa.email : 'Desconhecido' }" /></em><br/>
						<a4j:outputPanel rendered="#{ not empty turmaVirtual.turma.subturmas }">
							<strong style="font-weight:bold;">Turma:</strong> <em><h:outputText value="#{ discenteSelecionado.turma.codigo }" /></em>
						</a4j:outputPanel>
						
						
						<h1 style="font=-weight:bold;background:#DFE8F6;margin-top:20px;margin-bottom:10px;">Perfil</h1>
						
						<h:outputText value="#{ discenteSelecionado.perfilDiscente != null ? discenteSelecionado.perfilDiscente.perfil : 'Perfil não informado' }" escape="false"/>
					</td>
					<td style="vertical-align:top;width:30px;text-align:center;">
						<a4j:outputPanel rendered="#{ discenteSelecionado.discente.usuario != null }">
							<a href="javascript://nop/" onclick="Mensagem.show(<%=""+br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM%>, '<h:outputText value="#{ discenteSelecionado.discente.usuario.login }" />');">
								<h:graphicImage value="/img/email_go.png" alt="Enviar Mensagem" title="Enviar Mensagem" />
							</a>
						</a4j:outputPanel>
					</td>
				</tr></table>
			</div>
		</a4j:outputPanel>
	</a4j:outputPanel>
</p:dialog>