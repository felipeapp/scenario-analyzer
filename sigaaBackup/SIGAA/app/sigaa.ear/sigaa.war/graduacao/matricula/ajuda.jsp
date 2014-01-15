<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Ajuda da Matrícula OnLine</title>
<link rel="stylesheet" media="all" href="/shared/css/ufrn.css" type="text/css" />
<link rel="stylesheet" media="print" href="/shared/css/ufrn_print.css"/>
<link rel="stylesheet" media="all" href="/sigaa/css/matricula.css" type="text/css" />
<style>
<!--
table.menuMatricula td{
	padding-bottom: 20px;
	text-align: justify;
}
table.menuMatricula td.operacao {
	vertical-align: top;
	text-align: center;
}
table.menuMatricula td.confirmacao {
	text-align: center;
}
-->
</style>
</head>
<body>
<br>
<f:view>
	<div id="wrapper-menu-matricula" style="background-color: white">
	<table id="menu-matricula">
		<tr>
			<td>
				<br/><p align="left">Você possui duas maneiras de selecionar as turmas que deseja se matricular:</p><br><br>
			</td>
		</tr>
		<tr>
		<td>
			<table class="menuMatricula">
			<c:if test="${matriculaGraduacao.discente.regular}">
				<tr>
					<td class="operacao">
						<img alt="" src="/sigaa/img/graduacao/matriculas/turmas_curriculo.png" /><br />
						<h:outputLabel style="vertical-align: top;" value="Ver as turmas da Estr. Curricular"/>
						<br/><br/><br/><br/><br/><br/><br/><br/>
					</td>
					<td>
						<p align="Justify">
						Nessa tela são exibidas todas as turmas abertas dos componentes curriculares da sua estrutura curricular.
						Nela você tem a opção de visualizar dados sobre cada componente e cada turma. Selecione as turmas desejadas
						e clique no botão ADICIONAR TURMAS no final da página. Você ainda tem a opção de buscar por turmas abertas
						de componentes que componham as expressões de pré-requisito, co-requisito e equivalência.<br />
						Mas atenção! Nem todas as turmas listadas nessa página você pode se matricular.
						Alguns motivos para isso são: falta de pré-requisito, você já pagou algum componente equivalente ou
						pode já estar matriculado. Passe o mouse no ícone do lado esquerdo da turma para saber o motivo.
						</p><br/>
					</td>
				</tr>
				<tr>
					<td class="operacao">
						<img alt="" src="/sigaa/img/graduacao/matriculas/turmas_equivalentes_curriculo.png"><br />
						Ver equivalentes a Estr. Curricular
						<br/><br/><br/><br/><br/><br/><br/>
					</td>
					<td>
						<p align="Justify">
						Nessa tela são exibidas todas as turmas abertas dos componentes curriculares equivalentes àqueles presentes na sua estrutura curricular.
						Nela você tem a opção de visualizar dados sobre cada componente e cada turma. Selecione as turmas desejadas
						e clique no botão ADICIONAR TURMAS no final da página. Você ainda tem a opção de buscar por turmas abertas
						de componentes que componham as expressões de pré-requisito, co-requisito e equivalência.<br />
						Mas atenção! Nem todas as turmas listadas nessa página você pode se matricular.
						Alguns motivos para isso são: falta de pré-requisito, você já pagou algum componente equivalente ou
						pode já estar matriculado. Passe o mouse no ícone do lado esquerdo da turma para saber o motivo.
						</p><br/>
					</td>
				</tr>
			</c:if>
			<c:if test="${!matriculaGraduacao.discente.tecnico}">
				<tr>
					<td class="operacao">
						<img alt="" src="/sigaa/img/graduacao/matriculas/outras_turmas.png"><br />
						Buscar Turmas Abertas
						<br/><br/>
					</td>
					<td>
						<p align="Justify">
						Nessa tela você pode buscar qualquer turma aberta de qualquer currículo dos cursos de graduação da ${ configSistema['siglaInstituicao'] }.
						Informe os critérios de busca e você poderá escolher entre as turmas que aparecerão no resultado. Mas lembre-se,
						o SIGAA só permite matrículas em componentes de acordo com o regulamento dos cursos de graduação da ${ configSistema['siglaInstituicao'] }.
						</p><br/>
					</td>
				</tr>
			</c:if>
			<tr>
				<td colspan="2">
					<br/><p align="left">Ao adicionar as turmas selecionadas você pode visualizá-las em:</p><br/><br/>
				</td>
			</tr>
			<tr>
			<td class="operacao">
				<img alt="" src="/sigaa/img/graduacao/matriculas/turmas_selecionadas.png"><br />
				Ver as turmas selecionadas
				<br/><br/><br/>
			</td>
			<td>
				<p align="Justify">
				Nessa tela é possível visualizar as turmas em que você está selecionando para se matricular. Aqui você
				pode ver a distribuição dos horários das turmas selecionadas. É nessa tela que você pode remover as turmas que você não
				desejar mais se matricular.<br>
				ATENÇÃO! As matrículas só serão cadastradas no sistema depois que você clicar em CONFIRMAR MATRÍCULAS
				</p><br/>
			</td>
			</tr>
			<tr>
				<td colspan="2">
					<br/><p align="left">Depois de selecionar as turmas desejadas:<br/><br/></p>
				</td>
			</tr>
			<tr>
			<td class="botoes confirmacao">
				<img alt="" src="/sigaa/img/graduacao/matriculas/salvar.gif"><br />
				Confirmar Matrículas
			</td>
			<td>
				<p align="Justify">
				Depois de selecionar as turmas que deseja se matricular nesse semestre clique em CONFIRMAR MATRÍCULAS.
				Pronto! Agora sua matrícula já está feita e você pode alterar quantas vezes quiser até o prazo final
				da matrícula OnLine. A coordenação do seu curso poderá fazer observações para lhe orientar sobre as turmas
				que você escolheu se matricular, você será notificado no seu email quando essas observações forem feitas,
				e poderá visualizá-las clicando em "Ver Orientações da Coordenação".
				</p><br/>
			</td>
			</tr>
			</table>
		</td>
		<td>
		</td>
		</tr>
	</table>
	</div>
</f:view>

</body>
</html>
