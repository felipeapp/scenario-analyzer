
<div class="pessoal-docente">
	<div class="foto">
		<c:if test="${usuario.idFoto != null}">
			<img src="${ctx}/verFoto?idArquivo=${usuario.idFoto}&key=${ sf:generateArquivoKey(usuario.idFoto) }" style="width: 100px; height: 125px"/>
		</c:if>
		<c:if test="${usuario.idFoto == null}">
			<img src="${ctx}/img/no_picture.png" width="128" height="125"/>
		</c:if>
	</div>
	<ul>
		<li>
			<a	href="<%="/sigaa/abrirCaixaPostal.jsf?sistema="+String.valueOf(Sistema.SIGAA)%>" >
				Mensagens
			</a>
		</li>
		<li>
			<a class="perfil" href="../perfil.jsf">
				Atualizar Foto e Perfil
			</a>
		</li>
		<li>
			<h:form>
				<h:commandLink id="meusDadosPessoais" action="#{ alteracaoDadosDiscente.iniciarAcessoDiscente}" value="Meus Dados Pessoais"/>
			</h:form>
		</li>
	</ul>
	<div class="clear"> </div>
</div>
<p class="info-docente">

	<span class="nome"> <small><b>${usuario.pessoa.nome}</b></small> </span>

	<c:if test="${portalDiscente.perfil != null}">
		<ufrn:format name="portalDiscente" property="perfil.descricao" type="texto"/>
	</c:if>
</p>
<h:form id="form_links">
<div id="agenda-docente" style="text-align: center">
	<h:commandButton id="forumTurmas" image="/img/portal-de-turmas.png" action="#{ forumMedio.listarForunsTurmaSerie }" rendered="#{ not portalDiscente.modoReduzido }"/> &nbsp;
	<h:commandButton id="avaliacaoInstitucional" image="/img/avaliacao.jpg" action="#{ avaliacaoInstitucional.iniciarDiscente }" rendered="#{ acesso.usuario.discenteAtivo.nivelStr == 'G' && empty acesso.usuario.discenteAtivo.polo }"/>
	<h:commandLink id="buscarComunidadeVirtual" actionListener="#{ buscarComunidadeVirtualMBean.criar }" rendered="#{ not portalDiscente.modoReduzido }" title="Buscar Comunidades Virtuais">
		<h:graphicImage value="/img/cv.gif" width="90" height="50" alt="Comunidade Virtual" style="border:1px solid #CCC; margin-left:9px;"></h:graphicImage>
	</h:commandLink> 
</div>
</h:form>

<br>

<%-- CALENDARIO ACADEMICO MÉDIO --%>
<c:if test="${usuario.discenteAtivo.medio}">
	<p style="text-align: center; font-size: x-small">
		<a href="${linkPublico.urlDownloadPublico}/calendario_universitario.pdf" target="_blank"> Calendário Acadêmico do Ensino Médio</a>
	</p>				
</c:if>

<%-- DADOS INSTITUCIONAIS --%>
<div id="agenda-docente">
	<h4> Dados Institucionais </h4>
	<table>
		<tr>
			<td> Matrícula: </td>
			<td> ${usuario.discenteAtivo.matricula} </td>
		</tr>
		<tr>
			<td> Curso: </td>
			<td> ${usuario.discenteAtivo.curso.nome} </td>
		</tr>
		<tr>
			<td> Nível: </td>
			<td> ${usuario.discenteAtivo.nivelDesc} </td>
		</tr>
		<tr>
			<td> Status: </td>
			<td> ${usuario.discenteAtivo.statusString} </td>
		</tr>
		<tr>
			<td> E-Mail: </td>
			<td>
				<ufrn:format type="texto" name="usuario" property="email" length="18" />
			</td>
		</tr>
		<tr>
			<td> Entrada: </td>
			<td> ${usuario.discenteAtivo.anoIngresso}</td>
		</tr>
		<tr>
			<td> Ingresso: </td>
			<td> ${usuario.discenteAtivo.formaIngresso.descricao}</td>
		</tr>
		
	</table>

</div>
