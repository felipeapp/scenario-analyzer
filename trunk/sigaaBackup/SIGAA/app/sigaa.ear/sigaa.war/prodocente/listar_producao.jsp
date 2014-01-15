<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/docente/menu_docente.jsp"%>
<h2 class="title"><ufrn:subSistema /> > Listar as Produ��es Intelectuais</h2>
<c:set var="dirBase" value="/prodocente/producao/" scope="session"/>
<h:form>
<table class="listagem">
	<tr>
		<td class="subFormulario" colspan="2">
		Publica��es
		</td>
	</tr>
	<tr>
		<td colspan="2">

			<table style="width:100%">
				<tr>
					<td align="center" width="16%">
						<h:commandButton style="border: 0;" image="/img/prodocente/Artigo.gif" action="#{artigo.listar}"/><br />
						Artigo, Peri�dicos, Jornais e Similares </td>
					<td align="center" width="16%">
						<h:commandButton style="border: 0;" image="/img/prodocente/Capitulos.gif" action="#{capitulo.listar}"/><br />
						Cap�tulo de Livros </td>
					<td align="center" width="16%">
						<h:commandButton style="border: 0;" image="/img/prodocente/Livro.gif" action="#{livro.listar}" /><br />
						Livros </td>
					<td align="center" width="16%">
						<h:commandButton style="border: 0;" image="/img/prodocente/publicacoesEmEvento.gif" action="#{publicacaoEvento.listar}" /><br />
						Participa��o em Eventos </td>
					<td align="center" width="16%">
						<h:commandButton style="border: 0;" image="/img/prodocente/TextoDidaticoDiscussao.gif" action="#{textoDidatico.listar}" /><br />
						Textos Did�ticos e Discuss�o </td>
				</tr>
			</table>


		</td>
	</tr>

	<tr>
		<td class="subFormulario" colspan="2">
		Art�sticas, Liter�ria e Visual
		</td>
	</tr>
	<tr>
		<td colspan="2">

			<table width="100%">
				<tr>
					<td align="center" width="25%">
					<h:commandButton style="border: 0;" image="/img/prodocente/AudioVisuais.gif" action="#{audioVisual.listar}" /><br />
					AudioVisuais </td>
					<td align="center" width="25%">
					<h:commandButton style="border: 0;" image="/img/prodocente/ExposicaoApresentacao.gif" action="#{exposicao.listar}" /><br />
					Exposi��o ou Apresenta��o Art�sticas </td>
					<td align="center" width="25%">
					<h:commandButton style="border: 0;" image="/img/prodocente/Montagem.gif" action="#{montagem.listar}" /><br />
					Montagens </td>
					<td align="center" width="25%">
					<h:commandButton style="border: 0;" image="/img/prodocente/ProgramacaoVisual.gif" action="#{programacao.listar}" /><br />
					Programa��o Visual </td>
				</tr>
			</table>


		</td>
	</tr>

	<tr>
		<td class="subFormulario">
		Tecnol�gicas
		</td>
		<td width="50%" class="subFormulario" style="border-left: 20px solid #FFF;">
		Bancas
		</td>
	</tr>
	<tr>
		<td>

			<table width="100%">
				<tr>
					<td align="center" width="50%">
					<h:commandButton style="border: 0;" image="/img/prodocente/Maquete.gif" action="#{maquetePrototipoOutro.listar}" /><br />
					Maquetes, Prot�tipos, Softwares e Outros </td>
					<td align="center" width="50%">
					<h:commandButton style="border: 0;" image="/img/prodocente/Patente.gif" action="#{patente.listar}" /><br />
					Patentes </td>
				</tr>
			</table>
		</td>

		<td>

			<table width="100%">
				<tr>
					<td align="center" width="50%">
					<h:commandButton style="border: 0;" image="/img/prodocente/banca_curso.gif" action="#{banca.listaCurso}" /><br />
					Trabalhos de conclus�o </td>
					<td align="center" width="50%">
					<h:commandButton style="border: 0;" image="/img/prodocente/banca_concurso.gif" action="#{banca.listaConcurso}" /><br />
					Comiss�es Julgadoras </td>
				</tr>
			</table>
		</td>

	</tr>

	<tr>
		<td class="subFormulario" colspan="2">
		Outras
		</td>
	</tr>

	<tr>
		<td colspan="2">

			<table>
				<tr>
					<td align="center" width="13%">
						<h:commandButton style="border: 0;" image="/img/prodocente/premioRecebido.gif" action="#{premioRecebido.listar}" /><br />
						Pr�mio Recebido</td>
					<td align="center" width="13%">
						<h:commandButton style="border: 0;" image="/img/prodocente/BolsaObtida.gif" action="#{bolsaObtida.listar}" /><br />
						Bolsas Obtidas </td>
					<td align="center" width="13%">
						<h:commandButton style="border: 0;" image="/img/prodocente/VisitaCientifica.gif" action="#{visitaCientifica.listar}" /><br />
						Visitas Cient�ficas </td>
					<td align="center" width="13%">
						<h:commandButton style="border: 0;" image="/img/prodocente/organizacaoEventos.gif" action="#{participacaoComissaoOrgEventos.listar}" /><br />
						Organiza��o de Eventos, Consultorias, Edi��o e Revis�o de Per�odicos </td>
					<td align="center" width="13%">
						<h:commandButton style="border: 0;" image="/img/prodocente/participacaoSociedadeCientificaCutural.gif" action="#{participacaoSociedade.listar}" /><br />
						Participa��o em Sociedades Cient�ficas e Culturais </td>
					<td align="center" width="13%">
						<h:commandButton style="border: 0;" image="/img/prodocente/colegiadosComissoes.gif" action="#{participacaoColegiadoComissao.listar}" /><br />
						Participa��o em Colegiados e Comiss�es </td>
				</tr>
			</table>
		</td>
	</tr>

</table>

</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>