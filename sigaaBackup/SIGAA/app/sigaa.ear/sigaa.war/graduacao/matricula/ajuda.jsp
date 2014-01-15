<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Ajuda da Matr�cula OnLine</title>
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
				<br/><p align="left">Voc� possui duas maneiras de selecionar as turmas que deseja se matricular:</p><br><br>
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
						Nessa tela s�o exibidas todas as turmas abertas dos componentes curriculares da sua estrutura curricular.
						Nela voc� tem a op��o de visualizar dados sobre cada componente e cada turma. Selecione as turmas desejadas
						e clique no bot�o ADICIONAR TURMAS no final da p�gina. Voc� ainda tem a op��o de buscar por turmas abertas
						de componentes que componham as express�es de pr�-requisito, co-requisito e equival�ncia.<br />
						Mas aten��o! Nem todas as turmas listadas nessa p�gina voc� pode se matricular.
						Alguns motivos para isso s�o: falta de pr�-requisito, voc� j� pagou algum componente equivalente ou
						pode j� estar matriculado. Passe o mouse no �cone do lado esquerdo da turma para saber o motivo.
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
						Nessa tela s�o exibidas todas as turmas abertas dos componentes curriculares equivalentes �queles presentes na sua estrutura curricular.
						Nela voc� tem a op��o de visualizar dados sobre cada componente e cada turma. Selecione as turmas desejadas
						e clique no bot�o ADICIONAR TURMAS no final da p�gina. Voc� ainda tem a op��o de buscar por turmas abertas
						de componentes que componham as express�es de pr�-requisito, co-requisito e equival�ncia.<br />
						Mas aten��o! Nem todas as turmas listadas nessa p�gina voc� pode se matricular.
						Alguns motivos para isso s�o: falta de pr�-requisito, voc� j� pagou algum componente equivalente ou
						pode j� estar matriculado. Passe o mouse no �cone do lado esquerdo da turma para saber o motivo.
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
						Nessa tela voc� pode buscar qualquer turma aberta de qualquer curr�culo dos cursos de gradua��o da ${ configSistema['siglaInstituicao'] }.
						Informe os crit�rios de busca e voc� poder� escolher entre as turmas que aparecer�o no resultado. Mas lembre-se,
						o SIGAA s� permite matr�culas em componentes de acordo com o regulamento dos cursos de gradua��o da ${ configSistema['siglaInstituicao'] }.
						</p><br/>
					</td>
				</tr>
			</c:if>
			<tr>
				<td colspan="2">
					<br/><p align="left">Ao adicionar as turmas selecionadas voc� pode visualiz�-las em:</p><br/><br/>
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
				Nessa tela � poss�vel visualizar as turmas em que voc� est� selecionando para se matricular. Aqui voc�
				pode ver a distribui��o dos hor�rios das turmas selecionadas. � nessa tela que voc� pode remover as turmas que voc� n�o
				desejar mais se matricular.<br>
				ATEN��O! As matr�culas s� ser�o cadastradas no sistema depois que voc� clicar em CONFIRMAR MATR�CULAS
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
				Confirmar Matr�culas
			</td>
			<td>
				<p align="Justify">
				Depois de selecionar as turmas que deseja se matricular nesse semestre clique em CONFIRMAR MATR�CULAS.
				Pronto! Agora sua matr�cula j� est� feita e voc� pode alterar quantas vezes quiser at� o prazo final
				da matr�cula OnLine. A coordena��o do seu curso poder� fazer observa��es para lhe orientar sobre as turmas
				que voc� escolheu se matricular, voc� ser� notificado no seu email quando essas observa��es forem feitas,
				e poder� visualiz�-las clicando em "Ver Orienta��es da Coordena��o".
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
