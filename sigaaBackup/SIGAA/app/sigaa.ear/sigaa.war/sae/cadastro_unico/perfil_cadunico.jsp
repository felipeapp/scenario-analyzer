<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2> <ufrn:subSistema /> > Perfil do Aluno</h2>

	<a4j:keepAlive beanName="buscaDiscenteCadastroUnico" />

	<div class="descricaoOperacao">
	<p>
		O perfil do Aluno é uma operação disponível no Portal do Discente e na Adesão ao Cadastro Único que possibilita ao aluno criar um registro de seus interesses acadêmicos e outras qualidades relevantes a sua condição de discente.
	</p>
	
	<br/>
	
	<p>
		Esta funcionalidade permite que o professor veja o perfil do aluno que aderiu ao cadastro único. A intenção é facilitar a procura de alunos com determinado perfil, quando o professor estiver procurando alunos para suas bases de pesquisa. 
	</p>
	
	<br />

	<p>
		Caso tenha interesse de entrar em contato com o aluno é so preencher o campo no fim do formulário com sua mensagem. O aluno irá receber a mensagem na caixa postal dele e poderá dar continuidade ao diálogo. 
	</p>
	
	</div>

	<h:form>
		<table class="formulario" style="width: 85%">
			<caption>Perfil do Aluno</caption>
			<tr>
				<td class="subFormulario" colspan="3">${buscaDiscenteCadastroUnico.discente.nome }</td>
			</tr>
			<tr>
				<td rowspan="3" width="5%" align="center">
					<c:if test="${buscaDiscenteCadastroUnico.discente.idFoto != null}">
						<img class="fotoPerfil" src="/sigaa/verFoto?idFoto=${buscaDiscenteCadastroUnico.discente.idFoto}&key=${ sf:generateArquivoKey(buscaDiscenteCadastroUnico.discente.idFoto) }" width="100" height="125" />
					</c:if>
					<c:if test="${buscaDiscenteCadastroUnico.discente.idFoto == null}">
						<img class="fotoPerfil" src="${ctx}/img/no_picture.png" width="100" height="125" />
					</c:if>
					<br />
				</td>	
			</tr>
		<tr>
			<td colspan="2">Curso:	<br/>
				<t:outputText value="#{buscaDiscenteCadastroUnico.discente.curso.descricao}"style="width: 95%"/>
			</td>
		</tr>			
		<tr class="linhaImpar">
			<td colspan="3">Descrição Pessoal:	<br/>
				<t:outputText value="#{buscaDiscenteCadastroUnico.discente.perfil.descricao}"style="width: 95%"/>
			</td>
		</tr>
		<tr>
			<td colspan="3">Áreas de Interesse:<br/>
				<t:outputText value="#{buscaDiscenteCadastroUnico.discente.perfil.areas}" style="width: 95%"/> 
			</td>
		</tr>
		<tr class="linhaImpar">
			<td colspan="3">Currículo Lattes:<br/>
				<t:outputText value="#{buscaDiscenteCadastroUnico.discente.perfil.enderecoLattes}" style="width: 96%"/>
			</td>
		</tr>
		<tr>
			<td colspan="3">
				&nbsp
			</td>
		</tr>		
		<tr>
			<td colspan="3" class="subFormulario">Enviar Mensagem</td>
		</tr>
		<tr>
			<td colspan="3">Título:</td>
		</tr>
		
		<tr>
			<td colspan="3" align="center">
				<h:inputText style="width: 91%" value="#{buscaDiscenteCadastroUnico.mensagem.titulo}" />
			</td>
		</tr>
		<tr>
			<td colspan="3">Corpo da Mensagem:</td>
		</tr>		
		<tr>
			<td colspan="3" align="center">
				<h:inputTextarea style="width: 90%" rows="5" value="#{buscaDiscenteCadastroUnico.mensagem.mensagem}" />
			</td>
		</tr>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton value="Enviar Mensagem" action="#{buscaDiscenteCadastroUnico.enviarMensagem}" />
					<h:commandButton value="<< Voltar" immediate="true" action="#{buscaDiscenteCadastroUnico.telaBusca}" />
					<h:commandButton value="Cancelar" immediate="true" action="#{buscaDiscenteCadastroUnico.cancelar}" onclick="#{confirm}" />
				</td>
			</tr>
		</tfoot>
 		</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
