<%--
	Esta p�gina exibe as informa��es de um t�pico de aula.
	� inclu�da no arquivo /ava/aulas.jsp
	Necessita de um objeto do tipo TopicoAula chamado item
 --%>
 
 <%--
 
 <embed src="http://player.vimeo.com/video/20244241"></embed>
 <embed src="http://www.youtube.com/v/M4lNfvhpevQ"></embed>
 
 --%>
 	

<a4j:outputPanel rendered="#{ aula.visivel || turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }" id="aulas">
	<a4j:outputPanel layout="block" id="topico_aula" styleClass="topico-aula" style="padding:10px; position: relative; margin-left: #{ 10 + (20 * aula.nivel)}px; background:rgb(#{not aula.visivel ? '240,240,240' : aula.cor})">
		<%-- Exibe o t�tulo do t�pico de aula --%>		
		<a4j:outputPanel layout="block" styleClass="titulo" id="titulo">
			<h:outputText value="#{ aula.descricao }" rendered="#{!aula.aulaCancelada}"/> <h:outputText value=" (#{ aula.dataFormatada } - " rendered="#{!aula.aulaCancelada}"/> <h:outputText value="#{ aula.dataFimFormatada }) " rendered="#{!aula.aulaCancelada}"/> 
		
			<h:outputText value="#{ aula.descricao }" rendered="#{aula.aulaCancelada}" style="color:red;font-weight:bold;"/> 
			<h:outputText value=" (#{ aula.dataFormatada }) " rendered="#{aula.aulaCancelada}" style="color:red;font-weight:bold;"/>
			
			<h:commandLink id="idExibirTopico" action="#{ topicoAula.exibirTopicoDiscente }" rendered="#{!aula.visivel && (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) }" styleClass="naoImprimir">
				<f:param name="id" value="#{ aula.id }" />
				<f:param name="voltar_para_a_turma" value="true" />
				<h:graphicImage value="/img/show.gif" title="Tornar t�pico vis�vel para os Discentes" alt="Tornar t�pico vis�vel para os Discentes" />
			</h:commandLink>
			
			<h:commandLink id="idEsconderTopico" style="position:relative;" action="#{ topicoAula.esconderTopicoDiscente }" rendered="#{ turmaVirtual.edicaoAtiva && (aula.visivel && (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente)) }" styleClass="naoImprimir">
				<f:param name="id" value="#{ aula.id }" />
				<f:param name="voltar_para_a_turma" value="true" />
				<h:graphicImage value="/img/hide.gif" title="Esconder t�pico para os Discentes" alt="Esconder t�pico para os Discentes" />
			</h:commandLink>
	
			<small><i>
				<h:commandLink id="idEditarTopico"  style="position:relative;" rendered="#{ turmaVirtual.edicaoAtiva && (turmaVirtual.docente  || permissaoAva.permissaoUsuario.docente) }" value="(Editar)" action="#{ topicoAula.editar }" styleClass="naoImprimir">
					<f:param name="id" value="#{ aula.id }" />
					<f:param name="paginaOrigem" value="portalPrincipal"/>
				</h:commandLink>
			</i></small>
		</a4j:outputPanel>
			
		<%-- Exibe as a��es que o docente pode realizar sobre o t�pico de aula --%>
		<a4j:outputPanel rendered="#{ turmaVirtual.edicaoAtiva && (turmaVirtual.docente  || permissaoAva.permissaoUsuario.docente)}"  styleClass="naoImprimir acao-topico" layout="block">
			<h:selectOneMenu onchange="lancarAcaoTopico(this);">
				<f:selectItems value="#{ topicoAula.acoesTopico }" />
			</h:selectOneMenu>
		</a4j:outputPanel>
		
		<%-- Exibe a foto do docente que ministrar� a aula. --%>
		<a4j:outputPanel rendered="#{aula.topicoPai == null && fn:length(turmaVirtual.turma.docentesTurmas) > 1 && aula.docentes != null && turmaVirtual.config.mostrarFotoTopico}">
			<a4j:repeat value="#{aula.docentes }" var="doc">
				<a4j:outputPanel layout="block" style="width: 45px;" >
					<a4j:outputPanel layout="block" styleClass="foto-docente" >
						<h:graphicImage value="#{ctx}/verFoto?idArquivo=#{doc.idFoto}&key=#{ sf:generateArquivoKey(doc.idFoto) }" style="width: 40px; height: 50px" alt="#{doc.pessoa.nome} (#{doc.login})" title="#{doc.pessoa.nome} (#{doc.login})" rendered="#{doc.idFoto != null}" />
						<h:graphicImage value="#{ctx}/img/no_picture.png" width="40" height="50" alt="#{doc.pessoa.nome} (#{doc.login})" title="#{doc.pessoa.nome} (#{doc.login})" rendered="#{doc.idFoto == null}" /><br />
					</a4j:outputPanel>
				</a4j:outputPanel>
			</a4j:repeat>
		</a4j:outputPanel>

	  <%-- Exibe o conte�do do t�pico de aula	--%>
	  <a4j:outputPanel layout="block" styleClass="conteudotopico" id="conteudo">
		
			<h:outputText value="#{aula.conteudo}" escape="false" />
		
			<%-- Exibe as enquetes do t�pico --%>
			<a4j:outputPanel rendered="#{ not empty aula.enquetes }">
				<a4j:repeat value="#{aula.enquetes}" var="enq">
					<a4j:outputPanel layout="block" styleClass="item" >					
						<h:commandLink id="idMostrarEnquete" action="#{ enquete.telaVotacao }">
							<h:outputText value=" #{ enq.pergunta }"/>
							<f:param name="id" value="#{ enq.id }"/>
						</h:commandLink>
						<h:outputText value="&nbsp;" escape="false"/>
						
						<%-- A��es para as enquetes --%>
						<a4j:outputPanel rendered="#{ turmaVirtual.edicaoAtiva && (turmaVirtual.docente || permissaoAva.permissaoUsuario.enquete || permissaoAva.permissaoUsuario.docente) }">
							<h:commandLink id="idEditarEnquete" action="#{ enquete.editar }" title="Editar Enquete" styleClass="naoImprimir">
									<h:graphicImage value="/ava/img/page_edit.png"/>
									<f:param name="id" value="#{ enq.id }"/>
									<f:param name="paginaOrigem" value="portalPrincipal"/>
							</h:commandLink>
							
							<h:commandLink id="idRemoverEnquete" action="#{ enquete.removerDaTurma }" styleClass="naoImprimir confirm-remover" title="Remover Enquete" onclick="return(confirm('Deseja realmente excluir esta enquete?'));">
								<f:param name="id" value="#{ enq.id }"/>
								<h:graphicImage value="/img/porta_arquivos/delete.gif" alt="Remover Enquete" />
								<f:param name="paginaOrigem" value="portalPrincipal"/>
							</h:commandLink>
						</a4j:outputPanel>
					</a4j:outputPanel>
				</a4j:repeat>
			</a4j:outputPanel>
		
			<%-- Se o t�pico de aula possui materiais associados --%>
			<%--
				Estes materiais s�o populados no m�todo getAulas da classe TopicoAulaMBean
			--%>
			<rich:dragIndicator id="dragIndicator" />
			<a4j:repeat var="material" value="#{aula.materiais}" id="listaMateriais">
				 <a4j:outputPanel id="dndPanel">
				 
			        <rich:dropSupport id="destinoDragDrop" acceptedTypes="material_aula_#{ aula.id }" dropValue="#{ material.material.id }" dropListener="#{ materialTurmaBean.moverMaterial }" reRender="formAva">
					  <a4j:outputPanel layout="block" styleClass="item" 
					  			style="margin-left: #{ (material.material.nivel * 20) }px; #{ turmaVirtual.edicaoAtiva ? 'padding:5px 5px 5px 20px; border-style:solid;border-width:1px; border-color:#BED6F8; border-radius:5px 5px 5px 5px; position:relative; background:url(/sigaa/ava/img/handle_part.jpg) repeat-y; background-position:0px 5px;' : '' }">

							<%-- Mover material --%>
				            <a4j:outputPanel rendered="#{ turmaVirtual.edicaoAtiva }">
	                            <rich:dragSupport dragIndicator="dragIndicator" dragType="material_aula_#{ aula.id }" dragValue="#{ material.material.id }" rendered="#{ turmaVirtual.edicaoAtiva && (turmaVirtual.docente || permissao.docente) }" >
	                            	<rich:dndParam name="label" value="#{ material.nome }" />
	                            </rich:dragSupport>
								<a4j:outputPanel layout="block" style="position:absolute;left:0;top:0;width:15px;height:100%;cursor:move;" title="Mover" rendered="#{ turmaVirtual.edicaoAtiva }"/>
	                        </a4j:outputPanel>
	                        <h:outputText value="&nbsp;" escape="false"/>
	                        
	                        <%-- �cone caracter�stico do material --%>
                            <h:graphicImage value="#{ material.icone }" rendered="#{ (!material.tipoRotulo && !material.tipoQuestionario) || (material.tipoQuestionario && material.finalizado) || (!material.tipoRotulo && (turmaVirtual.docente || permissao.docente))  }"/>
	                        <h:outputText value="&nbsp;" escape="false"/>

							<%-- Se o material � um Arquivo --%>
							<a4j:outputPanel rendered="#{ material.tipoArquivo }">							
								<h:commandLink id="idInserirMaterialArquivo" action="#{ arquivoUsuario.baixarArquivoPortalPrincipal }" rendered="#{not empty material.arquivo }" target="_blank">
									<h:outputText value="#{ material.nome }" />
									<f:param name="id" value="#{ material.arquivo.idArquivo }"/>
								</h:commandLink>
								<h:outputText value="&nbsp;" escape="false"/>
									
								<%-- A��es para os Arquivos --%>
								<a4j:outputPanel rendered="#{ turmaVirtual.edicaoAtiva && (turmaVirtual.docente || permissao.inserirArquivo || permissaoAva.permissaoUsuario.docente) }">				 
									<h:commandLink id="idAtualizarMaterialArquivo" action="#{ atualizaArquivoTurmaMBean.preAtualizaAssociacaoArquivo }" title="Atualizar Arquivo" styleClass="naoImprimir">
										<h:graphicImage value="/img/refresh-small.png" width="12" alt="Atualizar Arquivo" />
										<f:param name="tid" value="#{turmaVirtual.turma.id}"/>
										<f:param name="id" value="#{ material.id }"/>
										<f:param name="paginaOrigem" value="portalPrincipal"/>
									</h:commandLink>
									<h:outputText value="&nbsp;" escape="false"/>
										
									<h:commandLink id="idRemoverMaterialArquivo" action="#{ arquivoUsuario.removerAssociacaoArquivo }"  onclick="return(confirm('Deseja realmente excluir este arquivo?'));" title="Remover Arquivo" styleClass="naoImprimir">
										<h:graphicImage value="/img/porta_arquivos/delete.gif" alt="Remover Arquivo" />
										<f:param name="id" value="#{ material.id }"/>
										<f:param name="paginaOrigem" value="portalPrincipal"/>
									</h:commandLink>
									<h:outputText value="&nbsp;" escape="false"/>
									
									<h:commandLink id="idRelatorioAcessoArquivo" action="#{ relatorioAcessoTurmaVirtualMBean.gerarRelatorioArquivosAcessadosUsuarios }" title="Relat�rio de Acessos ao Arquivo" styleClass="naoImprimir"
										rendered="#{not empty material.arquivo}">
										<h:graphicImage value="/img/comprovante.png"/>
										<f:param name="id" value="#{ material.arquivo.idArquivo }"/>
										<f:param name="paginaOrigem" value="portalPrincipal"/>
									</h:commandLink>
								</a4j:outputPanel>							
							</a4j:outputPanel>
					
							<%-- Se o material � uma Indica��o de Refer�ncia --%>
							<a4j:outputPanel rendered="#{ material.tipoIndicacao }">
								<h:commandLink id="idMostrarMaterialReferencia" action="#{ indicacaoReferencia.mostrar }" rendered="#{!material.site}">
									<h:outputText value=" #{ material.nome }"/>
									<f:param name="id" value="#{ material.id }"/>
									<f:param name="paginaOrigem" value="portalPrincipal"/>
								</h:commandLink>
								<h:outputText value="&nbsp;" escape="false"/>
								
								<h:outputLink value="#{material.url}" target="_blank" rendered="#{material.site}" styleClass="naoImprimir">
									<h:outputText value=" #{ material.nome }" escape="false" />
								</h:outputLink>
								<h:outputText value=" (#{material.tipoDesc})" />
								<h:outputText value="&nbsp;" escape="false"/>
								
								<%-- A��es para as indica��es de refer�ncia: Docente --%>
								<a4j:outputPanel rendered="#{ turmaVirtual.edicaoAtiva && (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) }">
									<h:commandLink id="idEditarMaterialReferencia" action="#{ indicacaoReferencia.editar }" title="Editar Refer�ncia" styleClass="naoImprimir">
											<h:graphicImage value="/ava/img/page_edit.png"/>
											<f:param name="id" value="#{ material.id }"/>
											<f:param name="paginaOrigem" value="portalPrincipal"/>  
									</h:commandLink>
									<h:outputText value="&nbsp;" escape="false"/>
									
									<h:commandLink id="idRemoverMaterialReferencia" action="#{ indicacaoReferencia.remover }"  onclick="return(confirm('Deseja realmente excluir este registro?'));" title="Remover Refer�ncia" styleClass="naoImprimir">
										<h:graphicImage value="/img/porta_arquivos/delete.gif" alt="Remover Refer�ncia" />
										<f:param name="id" value="#{ material.id }"/>
									</h:commandLink>
								</a4j:outputPanel>
							</a4j:outputPanel>
								
							<%-- Se o material � uma Tarefa --%>
							<a4j:outputPanel rendered="#{ material.tipoTarefa }">							
								<h:commandLink id="idMostrarMaterialTarefa" action="#{ respostaTarefaTurma.avaliarTarefas }" rendered="#{ turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }">
									<h:outputText value=" #{ material.nome }"/>
									<f:param name="id" value="#{ material.id }"/>
								</h:commandLink>
								<h:outputText value="&nbsp;" escape="false"/>
								
								<%-- A��es para as tarefas: Discente --%>
								<h:commandLink id="idEnviarMaterialTarefa" action="#{ respostaTarefaTurma.enviarTarefa }" rendered="#{ turmaVirtual.discente }">									
									<h:outputText value=" #{ material.nome}"/> 
									<f:param name="id" value="#{ material.id }"/>
								</h:commandLink>
								<h:outputText value="&nbsp;" escape="false"/>
								
								<%-- A��es para as tarefas: Docente --%>
								<a4j:outputPanel rendered="#{ turmaVirtual.edicaoAtiva && (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) }">
									<h:commandLink id="idEditarMaterialTarefa" action="#{ tarefaTurma.editar }" title="Editar Tarefa" styleClass="naoImprimir">
										<h:graphicImage value="/ava/img/page_edit.png"/>
										<f:param name="id" value="#{ material.id }"/>
									</h:commandLink>
									<h:outputText value="&nbsp;" escape="false"/>
				
									<h:commandLink id="idRemoverMaterialTarefaNota" action="#{ tarefaTurma.remover }" onclick="return(confirm('Esta tarefa possui nota, caso ela seja removida, as notas desta tarefa na planilha de notas tamb�m ser�o. Deseja realmente excluir este item?'))" title="Remover Tarefa" styleClass="naoImprimir"
										rendered="#{ material.possuiNota }">
										<h:graphicImage value="/img/porta_arquivos/delete.gif" alt="Remover Tarefa" />
										<f:param name="id" value="#{ material.id }"/>
									</h:commandLink>
									<h:outputText value="&nbsp;" escape="false"/>
	
									<h:commandLink id="idRemoverMaterialTarefaSemNota" action="#{ tarefaTurma.remover }" onclick="return(confirm('Deseja realmente excluir esta tarefa?'));" title="Remover Tarefa" styleClass="naoImprimir"
										rendered="#{ !material.possuiNota }">
										<h:graphicImage value="/img/porta_arquivos/delete.gif" alt="Remover Tarefa" />
										<f:param name="id" value="#{ material.id }"/>
									</h:commandLink>
								</a4j:outputPanel>
							</a4j:outputPanel>
							
							<%-- Se o material � um Question�rio --%>
							<a4j:outputPanel rendered="#{ material.tipoQuestionario && (material.finalizado || turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) }">							
								<h:commandLink id="idMostrarMaterialQuestionario" action="#{ questionarioTurma.visualizarRespostas }" rendered="#{turmaVirtual.docente || permissaoAva.permissaoUsuario.docente}">									
									<h:outputText value=" #{ material.titulo }"/>
									<f:param name="id" value="#{ material.id }"/>
								</h:commandLink>
								<h:outputText value="&nbsp;" escape="false"/>
								
								<%-- A��es para os question�rios: Discente --%>
								<h:commandLink id="idEnviarMaterialQuestionario" action="#{ questionarioTurma.iniciarResponderQuestionario }" rendered="#{ turmaVirtual.discente }">									
									<h:outputText value=" #{ material.titulo}"/> 
									<f:param name="id" value="#{ material.id }"/>
								</h:commandLink>
								<h:outputText value="&nbsp;" escape="false"/>
								
								<%-- A��es para os question�rios: Docente --%>
								<a4j:outputPanel rendered="#{ turmaVirtual.edicaoAtiva && (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) }">
									<h:commandLink id="idEditarMaterialQuestionario" action="#{ questionarioTurma.alterarDadosDoQuestionario }" title="Editar Question�rio" styleClass="naoImprimir">
										<h:graphicImage value="/ava/img/page_edit.png"/>
										<f:param name="id" value="#{ material.id }"/>
									</h:commandLink>
									<h:outputText value="&nbsp;" escape="false"/>
				
									<h:commandLink id="idRemoverMaterialQuestionarioNota" action="#{ questionarioTurma.remover }" onclick="return(confirm('Este question�rio possui nota, caso seja removido, suas notas na planilha de notas tamb�m ser�o. Deseja realmente excluir este item?'))" title="Remover Question�rio" rendered="#{ material.possuiNota }"  styleClass="naoImprimir">
										<h:graphicImage value="/img/porta_arquivos/delete.gif" alt="Remover Question�rio" />
										<f:param name="voltar_para_a_turma" value="true" />
										<f:param name="id" value="#{ material.id }"/>
									</h:commandLink>
									<h:outputText value="&nbsp;" escape="false"/>
	
									<h:commandLink id="idRemoverMaterialQuestionarioSemNota" action="#{ questionarioTurma.remover }" onclick="return(confirm('Deseja realmente excluir este question�rio?'));" title="Remover Question�rio" rendered="#{ !material.possuiNota }" styleClass="naoImprimir">
										<h:graphicImage value="/img/porta_arquivos/delete.gif" alt="Remover Question�rio" />
										<f:param name="voltar_para_a_turma" value="true" />
										<f:param name="id" value="#{ material.id }"/>
									</h:commandLink>
								</a4j:outputPanel>
							</a4j:outputPanel>
							

							<%-- Se o material � um V�deo --%>
							<a4j:outputPanel rendered="#{ material.tipoVideo }">
								 		
									<a4j:outputPanel rendered="#{ !material.telaCheia && (material.link == '' || material.link == null || material.linkVideo != null) }">
										<h1 style="font-weight:bold;font-size:14pt;margin-top:20px;display:inline;"><h:outputText value="#{ material.titulo }" /></h1>
									</a4j:outputPanel>
									
									<%-- Se for um v�deo externo --%>
									<a4j:outputPanel rendered="#{ material.link != '' && material.link != null }">
										<%-- Se for um v�deo para ser exibido em outra janela, exibe somente o link --%>
										<a4j:outputPanel rendered="#{ material.linkVideo != null && material.telaCheia }">
											<h:commandLink action="#{videoTurma.exibir}" target="_blank">
												<h:outputText value=' #{ material.titulo } ' />
												<f:param name="id" value="#{ material.id }" />
											</h:commandLink>
										</a4j:outputPanel>
										
										<%-- Se o docente enviou um v�deo com link n�o reconhecido, exibe o link para que o discente clique e assista ao v�deo na sua pr�pria p�gina. --%>
										<a4j:outputPanel rendered="#{ material.linkVideo == null }" styleClass="inline">
											<a href="<h:outputText value='#{ material.link }' />" target="_blank">
											<h:outputText value=' #{ material.titulo } (Link Externo)' /></a>
										</a4j:outputPanel>
									</a4j:outputPanel>
									
									<%-- Se for um v�deo enviado pelo docente, marcado para ser exibido em outra janela --%>
									<a4j:outputPanel rendered="#{ (material.link == '' || material.link == null) && material.telaCheia }">
										<h:commandLink action="#{videoTurma.exibir}" target="_blank">
											<h:outputText value=' #{ material.titulo } ' />
											<f:param name="id" value="#{ material.id }" />
										</h:commandLink>
									</a4j:outputPanel>
									
									<%-- A��es para os v�deos: Docente --%>
									<a4j:outputPanel rendered="#{ turmaVirtual.edicaoAtiva && (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) }">
										<h:commandLink id="idEditarMaterialVIdeo" action="#{ videoTurma.alterarVideo }" title="Editar V�deo">
											<h:graphicImage value="/ava/img/page_edit.png"/>
											<f:param name="voltarATurma" value="true"/>
											<f:param name="id" value="#{ material.id }"/>
										</h:commandLink>
										<h:outputText value="&nbsp;" escape="false"/>
					
										<h:commandLink id="idRemoverVideo" action="#{ videoTurma.remover }" onclick="return(confirm('Deseja realmente excluir este v�deo?'));" title="Remover V�deo">
											<h:graphicImage value="/img/porta_arquivos/delete.gif" alt="Remover V�deo" />
											<f:param name="id" value="#{ material.id }"/>
											<f:param name="voltarATurma" value="true"/>
										</h:commandLink>
										
										<h:commandLink id="idRelatorioAcessoVideo" action="#{ relatorioAcessoTurmaVirtualMBean.gerarRelatorioArquivosAcessadosUsuarios }" title="Relat�rio de Acessos ao V�deo" styleClass="naoImprimir"
											rendered="#{not empty material && (material.link == '' || material.link == null)}">
											<h:graphicImage value="/img/comprovante.png"/>
											<f:param name="id" value="#{  material.id }"/>
											<f:param name="video" value="true"/>
											<f:param name="paginaOrigem" value="portalPrincipal"/>
									</h:commandLink>	
									</a4j:outputPanel>								
							</a4j:outputPanel>


								
							<%-- Se o material � um Conte�do --%>
							<a4j:outputPanel rendered="#{ material.tipoConteudo }">								
								<h:commandLink id="idMostrarMaterialConteudo" action="#{ conteudoTurma.mostrar }">									
									<h:outputText value=" #{ material.nome }"/>
									<f:param name="id" value="#{ material.id }"/>
								</h:commandLink>
								<h:outputText value="&nbsp;" escape="false"/>
								
								<%-- A��es para os conte�dos: Docente --%>
								<a4j:outputPanel rendered="#{ turmaVirtual.edicaoAtiva && (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) }">
									<h:commandLink id="idEditarMaterialConteudo" action="#{ conteudoTurma.editar }" title="Editar Conte�do" styleClass="naoImprimir">
											<h:graphicImage value="/ava/img/page_edit.png"/>
											<f:param name="id" value="#{ material.id }"/>
											<f:param name="paginaOrigem" value="portalPrincipal"/>  
									</h:commandLink>
									<h:outputText value="&nbsp;" escape="false"/>
									
									<h:commandLink id="idRemoverMaterialConteudo" action="#{ conteudoTurma.inativar }" title="Remover Conte�do" onclick="return(confirm('Deseja realmente excluir este conte�do?'));" styleClass="naoImprimir">
										<h:graphicImage value="/img/porta_arquivos/delete.gif"/>
										<f:param name="id" value="#{ material.id }"/>
										<f:param name="paginaOrigem" value="portalPrincipal"/>
									</h:commandLink>
								</a4j:outputPanel>
							</a4j:outputPanel>

							
							<%-- Se o material � um r�tulo --%>
							<a4j:outputPanel rendered="#{ material.tipoRotulo }">
								<h:outputText value="#{ material.descricao }" escape="false" rendered="#{ material.visivel || turmaVirtual.docente || permissaoAva.permissaoUsuario.docente }"/>
								<h:outputText value="&nbsp;"  escape="false"/>
								
									<%-- A��es para os r�tulos: Docente --%>
									<a4j:outputPanel rendered="#{ turmaVirtual.edicaoAtiva && (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) }">												
										<h:commandLink id="idEditarMaterialRotulo" action="#{ rotuloTurmaBean.editar }" title="Editar R�tulo" 
												styleClass="naoImprimir"	rendered="#{ turmaVirtual.docente }">
												<h:graphicImage value="/ava/img/page_edit.png"/>
												<f:param name="id" value="#{ material.id }"/>
												<f:param name="paginaOrigem" value="portalPrincipal"/>  
										</h:commandLink>
										<h:outputText value="&nbsp;" escape="false"/>
										
										<h:commandLink id="idRemoverMaterialRotulo" action="#{ rotuloTurmaBean.inativar }" title="Remover R�tulo" 
											onclick="return(confirm('Deseja realmente excluir este r�tulo?'));" styleClass="naoImprimir"
											rendered="#{ (turmaVirtual.docente || permissao.docente) }">
											<h:graphicImage value="/img/porta_arquivos/delete.gif"/>
											<f:param name="id" value="#{ material.id }"/>
											<f:param name="paginaOrigem" value="portalPrincipal"/>
										</h:commandLink>
									</a4j:outputPanel>							
 							</a4j:outputPanel>		
	
	
							<%-- Se o material � um f�rum --%>
							<a4j:outputPanel rendered="#{ material.tipoForum }">							
								<h:commandLink id="idMostrarForum" action="#{ forumBean.view }">
									<h:outputText value=" #{ material.forum.titulo }"/>
									<f:setPropertyActionListener value="#{ material.forum.id }" target="#{ forumBean.obj.id }"/>
								</h:commandLink>
								<h:outputText value="&nbsp;" escape="false"/>
								
								<%-- A��es para os f�rums --%>
								<a4j:outputPanel rendered="#{ turmaVirtual.edicaoAtiva && (turmaVirtual.docente || permissaoAva.permissaoUsuario.forum || permissaoAva.permissaoUsuario.docente) }">
									<h:commandLink id="cmdEditarForum" action="#{ forumBean.atualizar }" styleClass="naoImprimir confirm-remover" title="Editar F�rum">
										<f:setPropertyActionListener target="#{ forumBean.obj }" value="#{ material.forum }" />
										<h:graphicImage value="/ava/img/page_edit.png" alt="Editar Item" />
									</h:commandLink>
									<h:outputText value="&nbsp;" escape="false"/>									

									<h:commandLink id="cmdRemoverForum" action="#{ forumTurmaBean.preRemover }" styleClass="naoImprimir confirm-remover" title="Remover F�rum">
										<f:setPropertyActionListener target="#{ forumTurmaBean.obj }" value="#{ material }" />
										<h:graphicImage value="/img/porta_arquivos/delete.gif" alt="Remover F�rum" />
									</h:commandLink>									
								</a4j:outputPanel>														
							</a4j:outputPanel>
							
							
							<%-- Exibe as enquetes do t�pico --%>
							<a4j:outputPanel rendered="#{ material.tipoEnquete }">							
								<h:commandLink id="idMostrarEnquete" action="#{ enquete.telaVotacao }">
									<h:outputText value=" #{ material.pergunta }"/>
									<f:param name="id" value="#{ material.id }"/>
								</h:commandLink>
								<h:outputText value="&nbsp;" escape="false"/>
										
								<%-- A��es para as enquetes --%>
								<a4j:outputPanel rendered="#{ turmaVirtual.edicaoAtiva && (turmaVirtual.docente || permissaoAva.permissaoUsuario.enquete || permissaoAva.permissaoUsuario.docente) }">
									<h:commandLink id="idEditarEnquete" action="#{ enquete.editar }" title="Editar Enquete" styleClass="naoImprimir">
											<h:graphicImage value="/ava/img/page_edit.png"/>
											<f:param name="id" value="#{ material.id }"/>
											<f:param name="paginaOrigem" value="portalPrincipal"/>
									</h:commandLink>
									<h:outputText value="&nbsp;" escape="false"/>
									
									<h:commandLink id="idRemoverEnquete" action="#{ enquete.removerDaTurma }" styleClass="naoImprimir confirm-remover" title="Remover Enquete" onclick="return(confirm('Deseja realmente excluir esta enquete?'));">
										<f:param name="id" value="#{ material.id }"/>
										<h:graphicImage value="/img/porta_arquivos/delete.gif" alt="Remover Enquete" />
										<f:param name="paginaOrigem" value="portalPrincipal"/>
									</h:commandLink>
								</a4j:outputPanel>
							</a4j:outputPanel>
							
							
							<%-- Se o material � um chat --%>
							<a4j:outputPanel rendered="#{ material.tipoChat }">
							
								<a4j:commandLink rendered="#{ not material.videoChat }" action="#{ chatTurmaBean.createChatParam }" oncomplete="var url = '/shared/EntrarChat?chatagendado=true&idchat=#{ material.id }&idusuario=#{ chatTurmaBean.usuarioLogado.id }&passkey=#{ chatTurmaBean.chatPassKey }&video=#{ material.videoChat }&chatName=#{ material.titulo }&origem=turmaVirtual&nomeUsuario=#{ turmaVirtual.usuarioLogado.pessoa.nome }'; window.open(url, 'chat_#{ material.id }', 'height=600,width=800,location=0,resizable=1'); return false;">
									<h:outputText value="#{ material.titulo }"/> 
									<f:param name="id" value="#{ material.id }"/>
								</a4j:commandLink>
							
								<a4j:commandLink rendered="#{ material.videoChat }" action="#{ chatTurmaBean.createChatParam }" oncomplete='exibirJanelaVideoChat("#{ material.id }", #{chatTurmaBean.usuarioLogado.id}, "#{chatTurmaBean.chatPassKey}", "#{ material.titulo }", "#{ turmaVirtual.usuarioLogado.pessoa.nome }", #{ (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) ? "true" : "false" }, "#{ turmaVirtual.enderecoServidorVideo }");'>
									<h:outputText value="#{ material.titulo }"/> 
									<f:param name="id" value="#{ material.id }"/>
								</a4j:commandLink>
								<h:outputText value="&nbsp;" escape="false"/>
								
								<%-- A��es para os chat --%>
								<a4j:outputPanel rendered="#{ turmaVirtual.edicaoAtiva && (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) }">
									<h:commandLink id="idEditarChat" action="#{ chatTurmaBean.editar }"  title="Editar chat">
										<f:param name="id" value="#{ material.id }"/>
										<h:graphicImage value="/ava/img/page_edit.png" alt="Editar chat" />
									</h:commandLink>
									<h:outputText value="&nbsp;" escape="false"/>

									<h:commandLink id="idRemoverChat" action="#{ chatTurmaBean.inativar }" styleClass="naoImprimir confirm-remover" title="Remover este chat" onclick="return(confirm('Deseja realmente excluir este chat?'));">
										<f:param name="id" value="#{ material.id }"/>
										<h:graphicImage value="/img/porta_arquivos/delete.gif" alt="Remover chat" />
									</h:commandLink>
								</a4j:outputPanel>														
							</a4j:outputPanel>
							<h:outputText value="&nbsp;" escape="false"/>
	
	
							<%-- Mover material --%>
							<a4j:outputPanel id="mover" rendered="#{ turmaVirtual.edicaoAtiva && (turmaVirtual.docente || permissaoAva.permissaoUsuario.docente) }">
	                   			<a4j:commandLink rendered="#{ material.material.nivel > 0 }" reRender="formAva" action="#{ materialTurmaBean.moverMaterial }" oncomplete="return true;">
	                   					<h:graphicImage value="/img/arrow_left.png" alt="Mover para esquerda" title="Mover para Esquerda"/>                    										
										<f:param name="id" value="#{ material.material.id }"/>
										<f:param name="movimento" value="paraEsquerda"/>
								</a4j:commandLink>
								<h:outputText value="&nbsp;" escape="false"/>
								
	                   			<a4j:commandLink reRender="formAva" action="#{ materialTurmaBean.moverMaterial }" oncomplete="return true;">
	                   					<h:graphicImage value="/img/arrow_right.png" alt="Mover para direita" title="Mover para Direita"/>
										<f:param name="id" value="#{ material.material.id }"/>
										<f:param name="movimento" value="paraDireita"/>
								</a4j:commandLink>
		           			</a4j:outputPanel>
							<h:outputText value="&nbsp;" escape="false"/>
									
							
							<a4j:outputPanel styleClass="descricao-item" layout="block">							
								<%-- Descri��o do arquivo --%>
								<a4j:outputPanel rendered="#{ material.tipoArquivo }">
									<h:outputText value="#{material.descricao}" escape="false" />
									<a4j:outputPanel rendered="#{ material.contentType == 'model/vrml' }">												
										<h:outputText value="<br/> Para visualizar este arquivo pode ser necess�ria a instala��o de um plug-in espec�fico." escape="false"/> 
										<h:outputLink value="http://www.cortona3d.com/Products/Cortona-3D-Viewer.aspx" target="_blank"><h:outputText value="Cortona3D Viewer" /></h:outputLink>
									</a4j:outputPanel>
								</a4j:outputPanel>
										
								<%-- Descri��o da tarefa --%>
								<a4j:outputPanel rendered="#{ material.tipoTarefa}">
									<h:outputText value="Inicia em " /> <h:outputText value="#{material.dataInicio}" /><h:outputText value=" �s #{ material.horaInicio }h #{ material.minutoInicio } e finaliza em " />
									<h:outputText value="#{material.dataEntrega}" /><h:outputText value=" �s #{ material.horaEntrega }h #{ material.minutoEntrega }" />
								</a4j:outputPanel>
										
								<%-- Descri��o do question�rio --%>
								<a4j:outputPanel rendered="#{ material.tipoQuestionario && (material.finalizado || turmaVirtual.docente || permissaoAva.permissaoUsuario.docente)}">
									<h:outputText value="Inicia em " /> <h:outputText value="#{material.inicio}" /><h:outputText value=" �s #{ material.horaInicio }h #{ material.minutoInicio } e finaliza em " />
									<h:outputText value="#{material.fim}" /><h:outputText value=" �s #{ material.horaFim }h #{ material.minutoFim }" />
								</a4j:outputPanel>
										
								<%-- Descri��o do v�deo --%>
								<a4j:outputPanel rendered="#{ material.tipoVideo}">
								
									<%-- Se for um v�deo enviado pelo docente e marcado para ser exibido na turma virtual --%>
									<a4j:outputPanel rendered="#{ (material.link == '' || material.link == null) && !material.telaCheia }">
										<a4j:outputPanel rendered="#{!material.converter || material.idArquivoConvertido != null}">
											<h:outputText value="<div class='video' id='player_#{material.id}' style='display:block;width:#{ material.largura }px;height:#{ material.altura }px;'></div>" escape="false" />
											
											<c:set var="idArquivoVideo" value='#{material.idArquivoConvertido != null ? material.idArquivoConvertido : material.idArquivo }' />
											
											<script>
												$f("player_<h:outputText value='#{material.id}'/>", "/sigaa/avaliacao/flowplayer/flowplayer-3.2.2.swf", {
													clip: {
														url: "/sigaa/verFoto?idFoto=<h:outputText value='#{idArquivoVideo}' />"
															+ escape("&key=<h:outputText value='#{ sf:generateArquivoKey(idArquivoVideo) }' />")
															+ escape("&salvar=false")
															,
												        onStart: function(clip){
													        var idRegistro = ".registrarVideo_<h:outputText value='#{material.id}'/>";
															J(idRegistro).trigger("click");
														},
														autoPlay: false
													},
													plugins: {
														controls: {
															volume:false,
															playlist:false,
															time:false
														}
													}
												});
											</script>
										</a4j:outputPanel>
										<a4j:outputPanel rendered="#{material.converter && material.idArquivoConvertido == null}">
											<a4j:outputPanel rendered="#{material.erro && turmaVirtual.docente}">
												<div style="font-weight:bold;background:#DDD;margin:10px;border:1px dashed #CCC;padding:10px;text-align:center;color:#CC0000;">Ocorreu um problema com a convers�o do v�deo. Provavelmente o formato enviado n�o � suportado. Por favor, converta o v�deo para outro formato e tente novamente.</div>
											</a4j:outputPanel>
											
											<a4j:outputPanel rendered="#{!material.erro || !turmaVirtual.docente}">
												<div style="font-weight:bold;background:#DDD;margin:10px;border:1px dashed #CCC;padding:10px;text-align:center;">O v�deo est� em processo de convers�o. Por favor, aguarde alguns minutos e acesse esta p�gina novamente.</div>
											</a4j:outputPanel>
										</a4j:outputPanel>
									</a4j:outputPanel>
									
									<%-- Se for um v�deo externo para ser exibido na turma virtual --%>
									<a4j:outputPanel rendered="#{ material.link != '' && material.link != null && !material.telaCheia && material.linkVideo != null }">
										<object width="<h:outputText value='#{ material.largura }' />" height="<h:outputText value='#{ material.altura }' />" >
											<param name="movie" value="<h:outputText value='#{ material.linkVideo }' />">
											<param name="allowFullScreen" value="true">
											<param name="allowscriptaccess" value="always">
											<param name="wmode" value="transparent">
											<embed src="<h:outputText value='#{ material.linkVideo }' />" type="application/x-shockwave-flash" allowscriptaccess="always" allowfullscreen="true" wmode="transparent" width="<h:outputText value='#{ material.largura }' />" height="<h:outputText value='#{ material.altura }' />">
										</object>
										<script>
											function verVideo () {
											 	var idRegistro = ".registrarVideo_<h:outputText value='#{material.id}'/>";
												J(idRegistro).trigger("click");				
											}	
										</script>
									</a4j:outputPanel>
									
									<h:outputText value="#{ material.descricao }" escape="false" />
									<a4j:commandLink styleClass="registrarVideo_#{material.id}" style="display:none;" action="#{videoTurma.verVideoPortalPrincipal}">
               							<f:param name="id" value="#{ material.id }"/>
									</a4j:commandLink>
								</a4j:outputPanel>
										
								<%-- Descri��o do chat--%>
								<a4j:outputPanel rendered="#{ material.tipoChat}">
									<h:outputText value="Inicia em " /> <h:outputText value="#{material.dataInicio}" /> <h:outputText value=" �s " /> 
									<h:outputText value="#{ material.horaInicio }"><f:convertDateTime pattern="HH:mm"/></h:outputText> e 
									<h:outputText value=" finaliza em " /> <h:outputText value="#{material.dataFim}" /><h:outputText value=" �s " /> 
									<h:outputText value="#{ material.horaFim }" ><f:convertDateTime pattern="HH:mm"/></h:outputText>
								</a4j:outputPanel>
							</a4j:outputPanel> <%-- Descri��o do item --%>
							
						 </a4j:outputPanel>																					
					  </rich:dropSupport>		                        
				  </a4j:outputPanel>
			   </a4j:repeat>
		   </a4j:outputPanel> <%-- Conte�do --%>		   		   	
		</a4j:outputPanel>
	   <h:panelGroup style="clear:both" layout="block" />
   </a4j:outputPanel> <%-- T�pico de Aula --%>

<script type="text/javascript">
function lancarAcaoTopico(elem) {
	if (elem.value.split('_')[1] > 0) {
		if(elem.value.split('_')[1] == 7 && !confirm('Deseja realmente remover o registro de aula?')) {
			elem.selectedIndex = 0; //Se o usu�rio cancelar a remo��o, setar o valor selecionado como o primeiro da lista.
			return null;			
		}
		
		acaoTopico(elem);
	}
}
</script>